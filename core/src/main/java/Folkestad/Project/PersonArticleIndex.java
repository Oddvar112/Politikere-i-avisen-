package Folkestad.Project;

import lombok.Getter;
import java.util.*;

/**
 * Holder oversikt over hvilke artikler hver person er nevnt i.
 */
public class PersonArticleIndex {
    @Getter
    private final Map<String, Set<String>> index = new HashMap<>();

    /**
     * Legger til en artikkellenke for en person.
     */
    public void addMention(String person, String articleUrl) {
        index.computeIfAbsent(person, k -> new HashSet<>()).add(articleUrl);
    }

    /**
     * Legger til flere personer for én artikkel.
     */
    public void addMentions(Collection<String> persons, String articleUrl) {
        for (String person : persons) {
            addMention(person, articleUrl);
        }
    }

    /**
     * Henter alle artikler en person er nevnt i.
     */
    public Set<String> getArticlesForPerson(String person) {
        return index.getOrDefault(person, Collections.emptySet());
    }
}
