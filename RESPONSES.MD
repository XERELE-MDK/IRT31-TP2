1. Différence entre IoC et DI et rôle du conteneur Spring

   IoC (Inversion of Control / Inversion de Contrôle) : C'est un principe de conception général qui consiste à inverser le flux de contrôle d'un programme. Au lieu que votre code appelle et gère directement la création et le cycle de vie des objets dont il a besoin, c'est un conteneur externe qui prend le contrôle et fournit ces objets au code au moment opportun.

   DI (Dependency Injection / Injection de Dépendances) : C'est un sous-ensemble, une technique concrète d'implémentation de l'IoC. Elle consiste à fournir (injecter) les dépendances d'un objet (ses objets collaborateurs) de l'extérieur plutôt que de les créer en interne.

En quoi Spring inverse le contrôle par rapport à new CricketCoach() :
Dans un code classique sans Spring, c'est votre classe qui décide de s'instancier elle-même en écrivant explicitement Coach myCoach = new CricketCoach();. Vous contrôlez la création. Avec Spring, vous déléguez cette responsabilité au conteneur : c'est Spring qui scanne les classes, les instancie en arrière-plan, gère leur cycle de vie et injecte automatiquement les dépendances requises là où elles sont demandées. Votre code ne fait que recevoir les objets prêts à l'emploi.
2. Pourquoi l’injection par constructeur est recommandée

L'injection par constructeur est privilégiée par rapport à l'injection par champ (@Autowired sur un attribut) pour plusieurs raisons majeures :

    Immuabilité : Les dépendances injectées via le constructeur peuvent être déclarées comme final. Cela garantit qu'elles ne pourront plus être modifiées après l'initialisation de l'objet, rendant le code plus sûr et thread-safe.

    Testabilité accrue : Pour tester une classe en Java pur (unit tests avec JUnit), il suffit d'instancier la classe en passant directement des mocks en paramètres du constructeur (new MyService(mockDependency)), sans avoir à démarrer tout le contexte Spring ni utiliser la réflexion.

    Prévention du NullPointerException : Un objet ne peut pas être instancié si ses dépendances obligatoires ne sont pas fournies. L'objet est donc toujours dans un état valide et totalement initialisé dès sa création (contrairement à l'injection par champ où le bean est d'abord créé avec des champs null avant que Spring n'injecte les valeurs).

3. La NoUniqueBeanDefinitionException

   Situation précise : On obtient cette exception lorsque Spring essaie d'injecter une dépendance par type (par exemple, une interface Coach), mais qu'il trouve plusieurs beans candidats éligibles dans son conteneur (par exemple CricketCoach et TennisCoach), sans savoir lequel choisir.

   Deux façons de la résoudre :

        Utiliser @Primary sur l'un des beans pour indiquer qu'il s'agit du choix par défaut prioritaire.

        Utiliser @Qualifier("nomDuBean") au point d'injection pour désigner explicitement le bean précis que l'on souhaite injecter.

   Priorité : Le @Qualifier est prioritaire sur @Primary. Si un point d'injection spécifie un @Qualifier, Spring l'utilisera en ignorant totalement le bean marqué @Primary.

4. Calcul et forçage de l’ID par défaut d’un bean

   Calcul de l’ID par défaut : Spring prend le nom de la classe, met la première lettre en minuscule (selon la convention standard JavaBeans) et en fait l'ID du bean. Par exemple, une classe nommée MySqlDataService donnera l'ID par défaut mySqlDataService. (Attention : si les deux premières lettres sont des majuscules, ex. RESTController, la casse est généralement préservée).

   Forçage manuel : On peut l'imposer explicitement en passant une valeur textuelle à l'annotation :

        Avec @Component("monIdPersonnalise")

        Avec @Bean(name = "monBeanSpecifique")

5. Avantages, inconvénients et parcimonie de @Lazy

   Avantage : L'initialisation paresseuse (@Lazy) évite de créer le bean au démarrage de l'application. Il ne sera instancié qu'à sa toute première utilisation (son premier appel ou injection effective). Cela permet d'accélérer le démarrage des applications lourdes et d'économiser de la mémoire pour des composants rarement sollicités.

   Inconvénient : Les erreurs de configuration, les problèmes de liaison ou les dépendances manquantes ne sont détectés qu'au moment de l'exécution (lors du premier appel du bean) et non au démarrage de l'application. Cela peut masquer des bugs critiques jusqu'à ce qu'un utilisateur interagisse avec la fonctionnalité en production.

   Pourquoi avec parcimonie : En règle générale, on privilégie la stratégie « Fail-fast » (détecter toutes les erreurs le plus tôt possible, c'est-à-dire au démarrage). Utiliser @Lazy partout reporte les erreurs et peut cacher des problèmes structurels.

6. Comportement du endpoint /check et portée du bean

   Ce qui a changé : Le bean sous-jacent a été configuré en portée prototype (@Scope("prototype")) au lieu de sa portée par défaut singleton.

   Ce que cela prouve : Cela prouve que le conteneur Spring ne partage plus la même instance unique (singleton) pour toute l'application. Avec un bean prototype, à chaque fois qu'une injection ou une demande est effectuée, Spring instancie un nouvel objet frais. La modification effectuée sur l'instance précédente n'impacte donc pas la nouvelle instance retournée, ce qui fait basculer le résultat de true à false.

7. Cycle de vie d’un bean Singleton et absence de @PreDestroy pour le prototype

   Ordre exact des étapes pour un bean Singleton :

        Instanciation du bean (appel du constructeur).

        Remplissage des propriétés et injection des dépendances (Population of properties).

        Aware interfaces (si implémentées, ex: BeanNameAware).

        Post-traitement avant initialisation (BeanPostProcessor.postProcessBeforeInitialization).

        Initialisation (appel de la méthode annotée @PostConstruct, ou InitializingBean.afterPropertiesSet(), ou init-method).

        Post-traitement après initialisation (BeanPostProcessor.postProcessAfterInitialization).

        Utilisation du bean par l'application.

        Destruction (lors de la fermeture propre du contexte Spring : appel de @PreDestroy, DisposableBean.destroy(), ou destroy-method).

   Pourquoi @PreDestroy n'est pas appelé pour un bean prototype :
   Spring gère de bout en bout le cycle de vie des beans singletons et conserve une référence sur eux pour pouvoir les détruire proprement à l'arrêt de l'application. En revanche, pour les beans prototypes, Spring se contente de les créer, de les configurer, de les livrer au client, puis oublie totalement leur existence et ne garde aucune référence en mémoire. Il est donc incapable de suivre leur fin de vie et de déclencher les méthodes @PreDestroy.

8. Cas de préférence de @Bean par rapport à @Component

On utilise @Bean (dans une classe @Configuration) plutôt que @Component sur la classe dans les situations suivantes :

    Utilisation d'une classe tierce (bibliothèque externe) : Lorsque vous souhaitez intégrer une classe provenant d'une bibliothèque dont vous ne possédez pas le code source (impossible d'y ajouter directement @Component, ex: configurer un ObjectMapper de Jackson ou un DataSource tiers).

    Logique d'instantiation complexe ou conditionnelle : Lorsque la création du bean nécessite du code procédural complexe, des calculs, ou des conditions dynamiques (ex: instancier un bean différent selon l'environnement de déploiement via @Profile ou @Conditional).

    Déclaration de multiples instances d'une même classe : Lorsque vous avez besoin de créer plusieurs beans distincts basés sur la même classe, mais configurés différemment (ex: configurer deux beans de type RestTemplate ou deux connexions de bases de données DataSource avec des URLs différentes).