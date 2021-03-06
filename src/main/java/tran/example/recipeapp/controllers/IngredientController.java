package tran.example.recipeapp.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tran.example.recipeapp.commands.IngredientCommand;
import tran.example.recipeapp.commands.RecipeCommand;
import tran.example.recipeapp.commands.UnitOfMeasureCommand;
import tran.example.recipeapp.services.IngredientService;
import tran.example.recipeapp.services.RecipeService;
import tran.example.recipeapp.services.UnitOfMeasureService;

@Slf4j
@Controller
public class IngredientController {

    private final IngredientService ingredientService;
    private final RecipeService recipeService;
    private final UnitOfMeasureService unitOfMeasureService;

    @Autowired
    public IngredientController(IngredientService ingredientService, RecipeService recipeService, UnitOfMeasureService unitOfMeasureService) {
        this.ingredientService = ingredientService;
        this.recipeService = recipeService;
        this.unitOfMeasureService = unitOfMeasureService;
    }
    @GetMapping
    @RequestMapping(RecipeController.RECIPE_BASE_URL + "/{recipeId}" + "/ingredients")
    public String getIngredients(@PathVariable String recipeId, Model model) {
        log.debug("getting ingredients for recipe with id: " + recipeId);
        model.addAttribute("recipe", recipeService.findCommandById(Long.valueOf(recipeId)));
        return "recipe/ingredient/list";
    }

    @GetMapping
    @RequestMapping(RecipeController.RECIPE_BASE_URL + "/{recipeId}" + "/ingredient" + "/{ingredientId}" + "/show")
    public String showIngredient(@PathVariable String recipeId, @PathVariable String ingredientId, Model model) {
        model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(Long.valueOf(recipeId),
                Long.valueOf(ingredientId)));
        return "recipe/ingredient/show";
    }

    @GetMapping
    @RequestMapping(RecipeController.RECIPE_BASE_URL + "/{recipeId}/ingredient/{id}/update")
    public String updateRecipeIngredient(@PathVariable String recipeId,
                                         @PathVariable String id, Model model){
        model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(Long.valueOf(recipeId), Long.valueOf(id)));

        model.addAttribute("uomList", unitOfMeasureService.getUnitOfMeasures());
        return "recipe/ingredient/ingredientform";
    }

    @PostMapping(RecipeController.RECIPE_BASE_URL + "/{recipeId}/ingredient")
    public String saveOrUpdate(@ModelAttribute IngredientCommand command, @PathVariable String recipeId){
        IngredientCommand savedCommand = ingredientService.saveIngredientCommand(command);

        log.debug("saved recipe id:" + savedCommand.getRecipeId());
        log.debug("saved ingredient id:" + savedCommand.getId());

        return "redirect:/recipe/" + savedCommand.getRecipeId() + "/ingredient/" + savedCommand.getId() + "/show";
    }

    @GetMapping(RecipeController.RECIPE_BASE_URL + "/{recipeId}/ingredient/new")
    public String newIngredient(@PathVariable String recipeId, Model model){

        //make sure we have a good id value
        RecipeCommand recipeCommand = recipeService.findCommandById(Long.valueOf(recipeId));
        //todo raise exception if null

        //need to return back parent id for hidden form property
        IngredientCommand ingredientCommand = new IngredientCommand();
        ingredientCommand.setRecipeId(recipeCommand.getId());
        model.addAttribute("ingredient", ingredientCommand);

        //init uom
        ingredientCommand.setUom(new UnitOfMeasureCommand());

        model.addAttribute("uomList",  unitOfMeasureService.getUnitOfMeasures());

        return "recipe/ingredient/ingredientform";
    }

    @GetMapping(RecipeController.RECIPE_BASE_URL + "/{recipeId}/ingredient/{ingredientId}/delete")
    public String deleteIngredientByRecipeIdAndIngredientId(@PathVariable String recipeId, @PathVariable String ingredientId) {
        log.debug("deleting ingredient with id: " + ingredientId);
        ingredientService.deleteByRecipeIdAndIngredientId(Long.valueOf(recipeId), Long.valueOf(ingredientId));
        return "redirect:/recipe/" + recipeId + "/ingredients";
    }
}
