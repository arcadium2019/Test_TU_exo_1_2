# TP — Développement d'une API bancaire en TDD, BDD et Intégration Continue

## Contexte

Une banque souhaite disposer d'une API REST permettant de gérer des comptes bancaires simples.

L'objectif est de développer l'application en appliquant les bonnes pratiques de qualité logicielle vues en formation :

* développement piloté par les tests (TDD) ;
* développement piloté par le comportement (BDD) ;
* automatisation des tests ;
* intégration continue avec GitHub Actions.

---

# Organisation du travail

Le projet devra être développé progressivement en respectant la démarche suivante :

1. création du projet Spring Boot ;
2. développement des fonctionnalités métier ;
3. écriture des tests unitaires ;
4. écriture des scénarios BDD ;
5. mise en place des rapports de tests ;
6. mise en place de GitHub Actions ;
7. publication du projet sur GitHub.

Des commits Git significatifs devront être réalisés tout au long du développement.

---

# Fonctionnalités attendues

## Gestion des comptes

L'application doit permettre :

### Création d'un compte

Un utilisateur doit pouvoir créer un compte bancaire.

Chaque compte possède :

* un numéro unique ;
* un titulaire ;
* un solde.

Le solde initial est fixé à 0.

---

### Consultation d'un compte

Un utilisateur doit pouvoir consulter un compte à partir de son numéro.

Si le compte n'existe pas, une erreur métier doit être retournée.

---

### Consultation de tous les comptes

Un utilisateur doit pouvoir obtenir la liste complète des comptes enregistrés.

---

### Dépôt d'argent

Un utilisateur doit pouvoir déposer de l'argent sur un compte.

Contraintes :

* le compte doit exister ;
* le montant doit être strictement positif.

---

### Retrait d'argent

Un utilisateur doit pouvoir retirer de l'argent d'un compte.

Contraintes :

* le compte doit exister ;
* le montant doit être strictement positif ;
* le solde doit être suffisant.

---

### Virement

Un utilisateur doit pouvoir effectuer un virement entre deux comptes.

Contraintes :

* les deux comptes doivent exister ;
* le montant doit être strictement positif ;
* le compte émetteur doit posséder les fonds nécessaires.

---

# API REST attendue

L'application devra exposer des endpoints REST permettant de réaliser toutes les opérations précédentes.

Le choix exact des routes REST est laissé libre tant qu'elles respectent les conventions REST courantes.

Exemples :

```text
POST   /accounts
GET    /accounts
GET    /accounts/{number}
POST   /accounts/{number}/deposit
POST   /accounts/{number}/withdraw
POST   /accounts/transfer
```

Ces routes sont données à titre indicatif.

---

# Travail demandé — Partie TDD

Développer les tests unitaires avant ou pendant l'implémentation métier.

Les tests doivent couvrir au minimum :

## Création de compte

* création réussie ;
* refus d'un numéro déjà existant.

## Consultation

* récupération d'un compte existant ;
* erreur si le compte est introuvable ;
* récupération de l'ensemble des comptes.

## Dépôt

* dépôt valide ;
* dépôt nul ;
* dépôt négatif.

## Retrait

* retrait valide ;
* retrait nul ;
* retrait négatif ;
* retrait avec fonds insuffisants.

## Virement

* virement valide ;
* virement nul ;
* virement négatif ;
* virement avec fonds insuffisants ;
* virement vers un compte inexistant ;
* virement depuis un compte inexistant.

---

## Bonnes pratiques attendues

Les tests doivent :

* respecter la structure Arrange / Act / Assert ;
* utiliser Mockito pour isoler la couche métier ;
* posséder des noms explicites ;
* être indépendants les uns des autres ;
* couvrir les cas nominaux et les cas d'erreur.

---

# Travail demandé — Partie BDD

Rédiger des scénarios Gherkin décrivant le comportement métier de l'application.

Les scénarios devront couvrir au minimum :

## Création de compte

```gherkin
Scenario: Création d'un nouveau compte
```

---

## Dépôt

```gherkin
Scenario: Dépôt d'argent sur un compte
```

---

## Retrait avec succès

```gherkin
Scenario: Retrait avec fonds suffisants
```

---

## Retrait refusé

```gherkin
Scenario: Retrait avec fonds insuffisants
```

---

## Virement

```gherkin
Scenario: Virement entre deux comptes
```

---

## Virement refusé

```gherkin
Scenario: Virement refusé pour solde insuffisant
```

---

Les scénarios devront être automatisés avec Cucumber.

Les Step Definitions devront être développées afin que tous les scénarios passent avec succès.

---

# Rapports de tests

Le projet devra produire au minimum :

- Rapport Cucumber
- Rapport JaCoCo

---

# Intégration Continue avec GitHub Actions

Le projet devra être hébergé sur GitHub.

Un workflow GitHub Actions devra être mis en place.

À chaque Push ou Pull Request :

* récupération du projet ;
* installation de Java 21 ;
* exécution des tests ;
* génération des rapports ;
* archivage des artefacts générés.

---

## Artefacts à publier

Le workflow devra conserver :

```text
target/surefire-reports
target/site/jacoco
target/cucumber-reports
```

---

# Livrables attendus

Le dépôt GitHub devra contenir :

* le code source complet ;
* les tests unitaires ;
* les scénarios Cucumber ;
* les Step Definitions ;
* le workflow GitHub Actions ;
* le rapport JaCoCo ;
* le rapport Cucumber ;
* un historique Git cohérent avec plusieurs commits significatifs.
