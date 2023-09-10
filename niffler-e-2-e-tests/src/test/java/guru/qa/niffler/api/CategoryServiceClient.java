package guru.qa.niffler.api;

import guru.qa.niffler.model.CategoryJson;

import java.io.IOException;

public class CategoryServiceClient extends RestService {

    public CategoryServiceClient() {
        super(config.nifflerCategoryUrl());
    }

    private final CategoryService categoryJson = retrofit.create(CategoryService.class);
    public CategoryJson addCategory(CategoryJson category) throws IOException {
        return categoryJson.addCategory(category).execute().body();
    }
}