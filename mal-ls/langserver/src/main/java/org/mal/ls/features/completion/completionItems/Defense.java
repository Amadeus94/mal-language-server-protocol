/** 
 * This class represents the completion item defence
 */
package org.mal.ls.features.completion.completionItems;

import org.eclipse.lsp4j.CompletionItemKind;

public class Defense extends CompletionItemMal {
    private static final String text = "# ";
    private static final String label = "defence";
    private static final String info = "As opposed to attack steps defenses are boolean. A defense step represents the countermeasure of an attack step (e.g., passwordBruteForce attack step can be defended by twoFactorAuthentications defense step). While declaring an asset, it is possible to either enable or disable a defense step by setting the defense step to either TRUE or FALSE.";
    private static final CompletionItemKind kind = CompletionItemKind.Operator;

    public Defense() {
        super(text, label, info, kind);
    }
}