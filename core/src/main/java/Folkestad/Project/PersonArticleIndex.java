package folkestad.project;

import lombok.Getter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Holder oversikt over hvilke artikler hver person er nevnt i.
 */
public class PersonArticleIndex {
    @Getter
    private final Map<String, Set<String>> index = new HashMap<>();

    /**
     * Legger til en artikkellenke for en person.
     *
     * @param person person som skal knyttes til artikkelen
     * @param articleUrl URL til artikkelen
     */
    public void addMention(final String person, final String articleUrl) {
        index.computeIfAbsent(person, k -> new HashSet<>()).add(articleUrl);
    }

    /**
     * Legger til flere personer for én artikkel.
     *
     * @param persons collection av personer som skal knyttes til artikkelen
     * @param articleUrl URL til artikkelen
     */
    public void addMentions(final Collection<String> persons, final String articleUrl) {
        for (String person : persons) {
            addMention(person, articleUrl);
        }
    }

    /**
     * Henter alle artikler en person er nevnt i.
     *
     * @param person personen som skal søkes etter
     * @return sett med artikkel-URLer hvor personen er nevnt
     */
    public Set<String> getArticlesForPerson(final String person) {
        return index.getOrDefault(person, Collections.emptySet());
    }

    /**
     * Henter alle personer som er registrert i indeksen.
     *
     * @return sett med alle personnavn
     */
    public Set<String> getAllPersons() {
        return index.keySet();
    }
}
