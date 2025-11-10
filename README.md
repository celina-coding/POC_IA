## Lancer l’application

##  Prérequis
Avoir **Java 17** ou une version supérieure et **Maven** installés, puis exécute la commande suivante à la racine du projet :
mvn spring-boot:run

L’application démarre par défaut sur : http://localhost:8080

## Tester l’API
curl -X POST http://localhost:8080/api/evaluation/test-rectangle \
     -H "Content-Type: application/json" \
     -d '{"description": "Rectangle bleu en haut à gauche"}'
## Éxemple de réponse 
{
    "type": "add-obj",
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "page-id": "00000000-0000-0000-0000-000000000000",
    "obj": {
        "type": "rect",
        "name": "Rectangle Bleu En Haut à Gauche",
        "x": 100,
        "y": 100,
        "width": 200,
        "height": 150,
        "selrect": {
            "x": 100, "y": 100, "width": 200, "height": 150,
            "x1": 100, "y1": 100, "x2": 300, "y2": 250
        },
        "points": [
            {"x": 100, "y": 100}, {"x": 300, "y": 100},
            {"x": 300, "y": 250}, {"x": 100, "y": 250}
        ],
        "transform": {
            "a": 1,
            "b": 0,
            "c": 0,
            "d": 1,
            "e": 0,
            "f": 0
        },
        "fills": [{"fill-color": "#0074D6"}]
    }
}