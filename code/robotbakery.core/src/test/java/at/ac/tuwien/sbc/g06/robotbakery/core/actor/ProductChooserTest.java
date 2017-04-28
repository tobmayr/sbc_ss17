package at.ac.tuwien.sbc.g06.robotbakery.core.actor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.ProductState;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.IngredientType;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IKneadRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;

public class ProductChooserTest {

	private IKneadRobotService kneadService = mock(IKneadRobotService.class);
	private Product baseDough1;
	private Product baseDough2;
	private TreeMap<IngredientType, Integer> ingredientStock;
	private Map<String, Integer> counterStock;

	@Before
	public void setUp() {
		baseDough1 = new Product(SBCConstants.PRODUCT1_NAME);
		baseDough1.setState(ProductState.DOUGH_IN_STORAGE);
		baseDough2 = new Product(SBCConstants.PRODUCT2_NAME);
		baseDough2.setState(ProductState.DOUGH_IN_STORAGE);
		ingredientStock = new TreeMap<IngredientType, Integer>(
				);
		counterStock = new HashMap<String, Integer>();

		when(kneadService.getCounterStock()).thenReturn(counterStock);
		when(kneadService.getIngredientStock()).thenReturn(ingredientStock);

	}

	@Test
	public void test_getBaseDoughFromStorage_canFinish() {
		ingredientStock.put(IngredientType.BAKING_MIX_SPICY, 2);
		when(kneadService.getBaseDoughsFromStorage()).thenReturn(Arrays.asList(baseDough1));

		ProductChooser chooser = new ProductChooser(kneadService);
		Product result = chooser.getFinishableBaseDough();
		assertEquals(baseDough1, result);

	}

	@Test
	public void test_getBaseDoughFromStorage_canNotFinish() {
		ingredientStock.put(IngredientType.BAKING_MIX_SPICY, 2);
		when(kneadService.getBaseDoughsFromStorage()).thenReturn(Arrays.asList(baseDough2));
		ProductChooser chooser = new ProductChooser(kneadService);
		Product result = chooser.getFinishableBaseDough();
		assertEquals(null, result);

	}

	@Test
	public void test_getNextProductForCounter_counterFull() {
		counterStock.put(SBCConstants.PRODUCT1_NAME, 10);
		counterStock.put(SBCConstants.PRODUCT1_NAME, 10);
		ProductChooser chooser = new ProductChooser(kneadService);
		Product result = chooser.getNextProductForCounter();
		assertEquals(null, result);

	}

	@Test
	public void test_getNextProductForCounter_shouldReturnProductWithSecondHighestDiff() {
		counterStock.put(SBCConstants.PRODUCT1_NAME, 3);
		counterStock.put(SBCConstants.PRODUCT2_NAME, 1);
		counterStock.put(SBCConstants.PRODUCT3_NAME, 7);
		counterStock.put(SBCConstants.PRODUCT4_NAME, 8);
		counterStock.put(SBCConstants.PRODUCT5_NAME, 5);

		ingredientStock.put(IngredientType.BAKING_MIX_SPICY, 1);
		ingredientStock.put(IngredientType.FLOUR, 500);
		ingredientStock.put(IngredientType.BAKING_MIX_SWEET, 5);
		ingredientStock.put(IngredientType.EGGS, 15);

		ProductChooser chooser = new ProductChooser(kneadService);
		Product result = chooser.getNextProductForCounter();
		assertEquals(SBCConstants.PRODUCT1_NAME, result.getProductName());

	}
	
	@Test
	public void test_getNextProductForCounter_shouldReturnProductLastSuitableProduct() {
		counterStock.put(SBCConstants.PRODUCT1_NAME, 9);
		counterStock.put(SBCConstants.PRODUCT2_NAME, 2);
		counterStock.put(SBCConstants.PRODUCT3_NAME, 8);
		counterStock.put(SBCConstants.PRODUCT4_NAME, 2);
		counterStock.put(SBCConstants.PRODUCT5_NAME, 1);

		ingredientStock.put(IngredientType.BAKING_MIX_SPICY, 15);
		ingredientStock.put(IngredientType.FLOUR, 100);
		ingredientStock.put(IngredientType.BAKING_MIX_SWEET, 5);
		ingredientStock.put(IngredientType.EGGS, 15);

		ProductChooser chooser = new ProductChooser(kneadService);
		Product result = chooser.getNextProductForCounter();
		assertEquals(SBCConstants.PRODUCT1_NAME, result.getProductName());

	}
	
	@Test
	public void test_getNextProductCounter_notEnoughIngredients(){
		counterStock.put(SBCConstants.PRODUCT1_NAME, 3);
		counterStock.put(SBCConstants.PRODUCT2_NAME, 1);
		counterStock.put(SBCConstants.PRODUCT3_NAME, 7);
		counterStock.put(SBCConstants.PRODUCT4_NAME, 8);
		counterStock.put(SBCConstants.PRODUCT5_NAME, 5);
		ingredientStock.put(IngredientType.BAKING_MIX_SPICY, 0);
		ingredientStock.put(IngredientType.FLOUR, 0);
		ingredientStock.put(IngredientType.BAKING_MIX_SWEET, 0);
		ingredientStock.put(IngredientType.EGGS, 0);
		ProductChooser chooser = new ProductChooser(kneadService);
		Product result = chooser.getNextProductForCounter();
		assertEquals(null, result);
	}
	
	@Test
	public void test_getNextProductForStorage_shouldChooseP3(){
		ingredientStock.put(IngredientType.BAKING_MIX_SPICY, 4);
		ingredientStock.put(IngredientType.FLOUR, 1000);
		ingredientStock.put(IngredientType.BAKING_MIX_SWEET, 15);
		ingredientStock.put(IngredientType.EGGS, 13);
			
		ProductChooser chooser = new ProductChooser(kneadService);
		Product result = chooser.getNextProductForStorage();
		assertEquals(SBCConstants.PRODUCT3_NAME, result.getProductName());
		
	
	}
	
	@Test
	public void test_getNextProductForStorage_shouldChooseNone(){
		ingredientStock.put(IngredientType.BAKING_MIX_SPICY, 0);
		ingredientStock.put(IngredientType.FLOUR, 1000);
		ingredientStock.put(IngredientType.BAKING_MIX_SWEET, 0);
		ingredientStock.put(IngredientType.EGGS, 1);
		
		ProductChooser chooser = new ProductChooser(kneadService);
		Product result = chooser.getNextProductForStorage();
		assertEquals(null, result);
		
	
	}
	
	
	@Test
	public void test_getNextBaseDoughForStorage_shouldChooseP5(){
		ingredientStock.put(IngredientType.BAKING_MIX_SPICY, 0);
		ingredientStock.put(IngredientType.FLOUR, 155);
		ingredientStock.put(IngredientType.BAKING_MIX_SWEET, 0);
		ingredientStock.put(IngredientType.EGGS, 0);
			
		ProductChooser chooser = new ProductChooser(kneadService);
		Product result = chooser.getNextBaseDoughForStorage();
		assertEquals(SBCConstants.PRODUCT5_NAME, result.getProductName());
		
	
	}
	
	@Test
	public void test_getNextBaseDoughForStorage_shouldChooseNoone(){
		ingredientStock.put(IngredientType.BAKING_MIX_SPICY, 0);
		ingredientStock.put(IngredientType.FLOUR, 99);
		ingredientStock.put(IngredientType.BAKING_MIX_SWEET, 0);
		ingredientStock.put(IngredientType.EGGS, 0);
			
		ProductChooser chooser = new ProductChooser(kneadService);
		Product result = chooser.getNextBaseDoughForStorage();
		assertEquals(null, result);
		
	
	}
	
}