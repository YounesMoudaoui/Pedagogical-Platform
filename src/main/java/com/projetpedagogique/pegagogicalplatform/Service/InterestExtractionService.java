package com.projetpedagogique.pegagogicalplatform.Service;


import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.stemmer.PorterStemmer;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import java.util.*;
import opennlp.tools.tokenize.SimpleTokenizer;

@Service
public class InterestExtractionService {

    // Dictionnaire des mots-clés associés à "informatique"
    private static final Map<String, List<String>> KEYWORD_ASSOCIATIONS = new HashMap<>() {{
        put("informatique", Arrays.asList(
                // Langages de programmation
                "java", "python", "c", "c++", "javascript", "typescript", "php", "ruby", "go", "rust", "swift",
                "kotlin", "scala", "perl", "matlab", "r", "vba", "bash", "shell", "sql", "pl/sql", "haskell",
                "lisp", "prolog", "f#", "groovy", "dart",
                // Bases de données
                "oracle", "mysql", "postgresql", "mongodb", "cassandra", "redis", "firebase", "sqlite",
                "mariadb", "couchbase", "neo4j", "elasticsearch", "influxdb", "hbase", "db2", "sybase",
                "teradata", "cosmosdb", "bigquery", "athena", "dynamodb",
                // Systèmes d'exploitation
                "linux", "windows", "macos", "ubuntu", "debian", "red hat", "centos", "fedora", "android",
                "ios", "unix", "freebsd", "openbsd", "solaris", "chrome os", "raspbian", "kali linux",
                // Concepts de programmation
                "algorithm", "structure de données", "file d'attente", "pile", "arbre", "graph", "table de hachage",
                "tri", "recherche", "récursivité", "programmation fonctionnelle", "programmation orientée objet",
                "héritage", "polymorphisme", "encapsulation", "abstraction", "lambda", "exception", "parallélisme",
                "thread", "concurrence", "asynchrone", "événement", "compilation", "interprétation", "bytecode",
                // Autres catégories à ajouter...
                "machine learning", "deep learning", "cloud computing", "iot", "cybersécurité", "big data"
        ));
    }};

    // Liste des stop words
    private static final List<String> STOP_WORDS = Arrays.asList(
            "de", "un", "une", "le", "la", "les", "à", "et", "ou", "des", "dans", "pour", "avec",
            "sur", "par", "au", "aux", "ce", "cet", "cette", "son", "sa", "ses", "leur", "leurs",
            "du", "que", "qui", "est", "ne", "pas", "plus", "il", "elle", "ils", "elles", "dont",
            "mais", "ni", "or", "si", "quand", "comme", "où"
    );

    public String extractInterests(String text) {
        // Tokenize the text to identify individual words
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] tokens = tokenizer.tokenize(text);

        // Container for relevant keywords
        Set<String> interests = new HashSet<>();

        // Process each token to check if it's a keyword in the domain of "informatique"
        for (String token : tokens) {
            token = token.toLowerCase().trim();

            // Ignore stop words
            if (!STOP_WORDS.contains(token)) {
                // Check if the token matches any of the keywords in the "informatique" domain
                List<String> relatedKeywords = KEYWORD_ASSOCIATIONS.get("informatique");
                if (relatedKeywords != null && relatedKeywords.contains(token)) {
                    interests.add(token);  // Add the token as an interest
                }
            }
        }

        // Returning the set of interests as a comma-separated string
        return String.join(", ", interests);
    }
}
