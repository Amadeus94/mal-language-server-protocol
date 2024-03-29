/** 
 * This class handles the instences of completion items
 */
package org.mal.ls.features.completion;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.Position;
import org.mal.ls.compiler.lib.AST;
import org.mal.ls.compiler.lib.AST.Asset;
import org.mal.ls.compiler.lib.AST.AttackStep;
import org.mal.ls.compiler.lib.AST.Category;
import org.mal.ls.compiler.lib.AST.Variable;
import org.mal.ls.features.completion.completionItems.AND;
import org.mal.ls.features.completion.completionItems.Abstract;
import org.mal.ls.features.completion.completionItems.Append;
import org.mal.ls.features.completion.completionItems.AssetItem;
import org.mal.ls.features.completion.completionItems.AssetSnippet;
import org.mal.ls.features.completion.completionItems.AssociationItem;
import org.mal.ls.features.completion.completionItems.AssociationSnippet;
import org.mal.ls.features.completion.completionItems.CategoryItem;
import org.mal.ls.features.completion.completionItems.CategorySnippet;
import org.mal.ls.features.completion.completionItems.Defense;
import org.mal.ls.features.completion.completionItems.Define;
import org.mal.ls.features.completion.completionItems.DeveloperInfo;
import org.mal.ls.features.completion.completionItems.Existence;
import org.mal.ls.features.completion.completionItems.Extends;
import org.mal.ls.features.completion.completionItems.Include;
import org.mal.ls.features.completion.completionItems.Intersection;
import org.mal.ls.features.completion.completionItems.LeadsTo;
import org.mal.ls.features.completion.completionItems.Let;
import org.mal.ls.features.completion.completionItems.ModelerInfo;
import org.mal.ls.features.completion.completionItems.NonExistence;
import org.mal.ls.features.completion.completionItems.OR;
import org.mal.ls.features.completion.completionItems.Require;
import org.mal.ls.features.completion.completionItems.Union;
import org.mal.ls.features.completion.completionItems.UserInfo;

public class CompletionItemsProvider {

    private List<CompletionItem> completionItems;

    public CompletionItemsProvider() {
        this.completionItems = new ArrayList<>();
        initItems();
    }

    private void initItems() {
        this.completionItems.add(new Abstract().ci);
        this.completionItems.add(new AND().ci);
        this.completionItems.add(new Append().ci);
        this.completionItems.add(new AssetItem().ci);
        this.completionItems.add(new AssetSnippet().ci);
        this.completionItems.add(new AssociationItem().ci);
        this.completionItems.add(new AssociationSnippet().ci);
        this.completionItems.add(new CategoryItem().ci);
        this.completionItems.add(new CategorySnippet().ci);
        this.completionItems.add(new Defense().ci);
        this.completionItems.add(new Define().ci);
        this.completionItems.add(new DeveloperInfo().ci);
        this.completionItems.add(new Existence().ci);
        this.completionItems.add(new Extends().ci);
        this.completionItems.add(new Include().ci);
        this.completionItems.add(new Intersection().ci);
        this.completionItems.add(new LeadsTo().ci);
        this.completionItems.add(new Let().ci);
        this.completionItems.add(new ModelerInfo().ci);
        this.completionItems.add(new NonExistence().ci);
        this.completionItems.add(new OR().ci);
        this.completionItems.add(new Require().ci);
        this.completionItems.add(new Union().ci);
        this.completionItems.add(new UserInfo().ci);
    }

    /**
     * Returns the created List containing all the completion items
     */
    public List<CompletionItem> getCompletionItems() {
        return this.completionItems;
    }

    /**
     * Iterates through the ast and adds completion items for entity names
     */
    public void addCompletionItemASTNames(AST ast, List<CompletionItem> completionItems) {
        List<Category> categories = ast.getCategories();
        categories.forEach((category) -> {
            completionItems.add(createCompletionItem(category.name.id));
            List<Asset> assets = category.getAssets();
            assets.forEach((asset) -> {
                completionItems.add(createCompletionItem(asset.name.id));
                List<Variable> letAsset = asset.getVariables();
                letAsset.forEach((let) -> {
                    completionItems.add(createCompletionItem(let.name.id));
                });
                List<AttackStep> attackSteps = asset.getAttacksteps();
                attackSteps.forEach((attackStep) -> {
                    completionItems.add(createCompletionItem(attackStep.name.id));
                });
            });
        });
    }

    private CompletionItem createCompletionItem(String text) {
        CompletionItem ci = new CompletionItem();
        ci.setInsertText(text);
        ci.setLabel(text);
        ci.setKind(CompletionItemKind.Text);
        return ci;
    }
}