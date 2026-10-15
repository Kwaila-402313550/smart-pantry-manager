package com.Kwaila.smartpantrymanger.db;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.Kwaila.smartpantrymanger.model.PantryItem;
import com.Kwaila.smartpantrymanger.model.Recipe;
import com.Kwaila.smartpantrymanger.model.RecipeIngredient;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {PantryItem.class, Recipe.class, RecipeIngredient.class},
        version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract PantryDao pantryDao();
    public abstract RecipeDao recipeDao();

    private static volatile AppDatabase INSTANCE;
    // Room must not run queries on the UI thread, so we use a background executor
    public static final ExecutorService dbExecutor = Executors.newFixedThreadPool(2);

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "pantry_db")
                            .addCallback(seedCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /** Runs once, when the database file is first created. */
    private static final RoomDatabase.Callback seedCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            dbExecutor.execute(() -> seedRecipes(INSTANCE.recipeDao()));
        }
    };

    private static void addRecipe(RecipeDao dao, String name, String steps, Object[][] ings) {
        int id = (int) dao.insertRecipe(new Recipe(name, steps));
        List<RecipeIngredient> list = new ArrayList<>();
        for (Object[] i : ings) {
            list.add(new RecipeIngredient(id, (String) i[0], ((Number) i[1]).doubleValue(), (String) i[2]));
        }
        dao.insertIngredients(list);
    }

    private static void seedRecipes(RecipeDao dao) {
        addRecipe(dao, "Tomato Omelette",
                "1. Beat the eggs.\n2. Chop the tomatoes.\n3. Fry in butter until set.",
                new Object[][]{{"egg", 3, "pcs"}, {"tomato", 1, "pcs"}, {"butter", 10, "g"}});

        addRecipe(dao, "Buttered Toast",
                "1. Toast the bread.\n2. Spread the butter.",
                new Object[][]{{"bread", 2, "pcs"}, {"butter", 10, "g"}});

        addRecipe(dao, "Plain Rice Bowl",
                "1. Rinse the rice.\n2. Boil in water for 15 minutes.\n3. Season with salt.",
                new Object[][]{{"rice", 200, "g"}, {"water", 400, "ml"}, {"salt", 2, "g"}});


    }
}