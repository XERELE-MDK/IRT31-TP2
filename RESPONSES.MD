# Réponses aux questions sur Spring Framework

---

### 1. IoC vs DI et l'inversion de contrôle

* **IoC (Inversion of Control) :** Principe d'architecture général où le contrôle de la création et de la gestion des objets est délégué à un framework au lieu d'être géré par votre code.
* **DI (Dependency Injection) :** Technique concrète qui réalise l'IoC en fournissant (injectant) aux classes les dépendances dont elles ont besoin.
* **Différence avec `new CricketCoach()` :** Avec `new`, votre code contrôle directement l'instanciation. Avec Spring, le conteneur prend le contrôle : il instancie, configure et injecte `CricketCoach` là où il faut.

---

### 2. Avantages de l'injection par constructeur

* **Immuabilité :** Permet de déclarer les dépendances avec le mot-clé `final`, garantissant qu'elles ne seront plus modifiées après l'instanciation.
* **Testabilité :** Facilite les tests unitaires : vous pouvez instancier la classe directement en lui passant des *mocks* sans avoir besoin de démarrer un contexte Spring.
* **Fiabilité :** Empêche l'instanciation d'un composant dans un état incomplet avec des dépendances à `null`.

---

### 3. `NoUniqueBeanDefinitionException` et résolutions

* **Situation :** Spring trouve **plusieurs beans** du même type compatibles dans le conteneur et ne sait pas lequel injecter.
* **Solution 1 :** Ajouter `@Primary` sur la classe du bean à choisir par défaut.
* **Solution 2 :** Ajouter `@Qualifier("nomDuBean")` au point d'injection.
* **Priorité :** `@Qualifier` est **prioritaire** sur `@Primary`.

---

### 4. Calcul et personnalisation de l'id d'un bean

* **Calcul par défaut :** Le nom de la classe avec la première lettre en minuscule (ex. `CricketCoach` $\rightarrow$ `cricketCoach`).
* **Forcer manuellement :** Spécifier le nom directement dans l'annotation, ex. `@Component("monCoach")` ou `@Bean("monCoach")`.

---

### 5. Avantages et inconvénients de `@Lazy`

* **Avantages :** Démarrage plus rapide de l'application et économie de mémoire si le bean n'est jamais sollicité.
* **Inconvénients :** Décale la détection des erreurs de configuration du démarrage vers le moment où le bean est demandé pour la première fois.
* **Pourquoi avec parcimonie :** Risque de masquer des bugs jusqu'en production et introduit un temps de retard (latence) pour le premier utilisateur qui déclenche sa création.

---

### 6. Portée du bean (Scope)

* **Changement effectué :** Le scope du bean a été modifié de **`singleton`** (par défaut) à **`prototype`**.
* **Ce que cela prouve :** 
  * En `singleton`, l'instance est unique et partagée : la modification persiste d'un appel à l'autre (`true`).
  * En `prototype`, Spring crée une nouvelle instance à chaque demande, réinitialisant l'état du bean pour la requête suivante (`false`).

---

### 7. Cycle de vie du bean et `@PreDestroy`

**Ordre d'un bean singleton :**
1. Instanciation (Constructeur)
2. Injection des dépendances
3. Méthodes d'initialisation (`@PostConstruct`)
4. Bean prêt à l'emploi
5. Méthode de destruction (`@PreDestroy`) lors de l'arrêt du conteneur

* **Pourquoi pas `@PreDestroy` sur un `prototype` ?**
Spring crée et injecte le bean prototype, puis le livre au client sans conserver de référence en mémoire. Ne suivant plus son cycle de vie, le conteneur ne peut pas appeler `@PreDestroy` (la destruction est gérée par le Garbage Collector Java).

---

### 8. Trois cas d'utilisation de `@Bean` (dans `@Configuration`)

1. **Bibliothèques tierces :** Instancier une classe externe dont vous ne possédez pas le code source (impossible d'y mettre `@Component`).
2. **Instances multiples :** Créer plusieurs beans du même type configurés différemment.
3. **Logique d'initialisation complexe :** Nécessiter du code Java conditionnel ou avancé avant de retourner l'instance du bean.
