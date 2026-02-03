package es.upm.grise.profundizacion.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import es.upm.grise.exceptions.IncorrectItemException;

public class OrderTest {
	
	private Order order;
	
	@BeforeEach
	public void setUp() {
		order = new Order();
	}
	
	@Test
	public void testConstructorInitializesEmptyItemsList() {
		// La lista de items debe estar vacía pero no nula
		assertNotNull(order.getItems());
		assertEquals(0, order.getItems().size());
	}
	
	@Test
	public void testAddItemWithValidData() throws IncorrectItemException {
		// Crear un item válido
		Item item = createMockItem(1L, 10.0, 2);
		
		// Añadir el item
		order.addItem(item);
		
		// Verificar que se ha añadido
		assertEquals(1, order.getItems().size());
		assertTrue(order.getItems().contains(item));
	}
	
	@Test
	public void testAddItemWithNegativePrice() {
		// Crear un item con precio negativo
		Item item = createMockItem(1L, -5.0, 2);
		
		// Debe lanzar IncorrectItemException
		assertThrows(IncorrectItemException.class, () -> {
			order.addItem(item);
		});
	}
	
	@Test
	public void testAddItemWithZeroPrice() throws IncorrectItemException {
		// Crear un item con precio cero (válido según la especificación >= 0)
		Item item = createMockItem(1L, 0.0, 2);
		
		// No debe lanzar excepción
		assertDoesNotThrow(() -> {
			order.addItem(item);
		});
		
		// Verificar que se ha añadido
		assertEquals(1, order.getItems().size());
	}
	
	@Test
	public void testAddItemWithZeroQuantity() {
		// Crear un item con cantidad cero
		Item item = createMockItem(1L, 10.0, 0);
		
		// Debe lanzar IncorrectItemException
		assertThrows(IncorrectItemException.class, () -> {
			order.addItem(item);
		});
	}
	
	@Test
	public void testAddItemWithNegativeQuantity() {
		// Crear un item con cantidad negativa
		Item item = createMockItem(1L, 10.0, -1);
		
		// Debe lanzar IncorrectItemException
		assertThrows(IncorrectItemException.class, () -> {
			order.addItem(item);
		});
	}
	
	@Test
	public void testAddItemSameProductSamePriceIncrementsQuantity() throws IncorrectItemException {
		// Crear un producto compartido
		Product product = new Product();
		product.setId(1L);
		
		// Crear dos items con el mismo producto y precio
		Item item1 = createMockItemWithProduct(product, 10.0, 2);
		Item item2 = createMockItemWithProduct(product, 10.0, 3);
		
		// Añadir el primer item
		order.addItem(item1);
		
		// Añadir el segundo item
		order.addItem(item2);
		
		// Debe haber solo un item en la lista
		assertEquals(1, order.getItems().size());
		
		// La cantidad del primer item debe haberse incrementado
		Item itemInOrder = order.getItems().iterator().next();
		assertEquals(5, itemInOrder.getQuantity()); // 2 + 3
	}
	
	@Test
	public void testAddItemSameProductDifferentPriceAddsNewItem() throws IncorrectItemException {
		// Crear un producto compartido
		Product product = new Product();
		product.setId(1L);
		
		// Crear dos items con el mismo producto pero diferente precio
		Item item1 = createMockItemWithProduct(product, 10.0, 2);
		Item item2 = createMockItemWithProduct(product, 15.0, 3);
		
		// Añadir ambos items
		order.addItem(item1);
		order.addItem(item2);
		
		// Deben estar ambos items en la lista
		assertEquals(2, order.getItems().size());
		assertTrue(order.getItems().contains(item1));
		assertTrue(order.getItems().contains(item2));
	}
	
	@Test
	public void testAddItemDifferentProductsAddsMultipleItems() throws IncorrectItemException {
		// Crear tres items con diferentes productos
		Item item1 = createMockItem(1L, 10.0, 2);
		Item item2 = createMockItem(2L, 20.0, 1);
		Item item3 = createMockItem(3L, 30.0, 5);
		
		// Añadir todos los items
		order.addItem(item1);
		order.addItem(item2);
		order.addItem(item3);
		
		// Deben estar los tres items en la lista
		assertEquals(3, order.getItems().size());
	}
	
	@Test
	public void testAddItemMultipleSameProductSamePriceCumulatesQuantity() throws IncorrectItemException {
		// Crear un producto compartido
		Product product = new Product();
		product.setId(1L);
		
		// Crear tres items con el mismo producto y precio
		Item item1 = createMockItemWithProduct(product, 10.0, 2);
		Item item2 = createMockItemWithProduct(product, 10.0, 3);
		Item item3 = createMockItemWithProduct(product, 10.0, 5);
		
		// Añadir todos los items
		order.addItem(item1);
		order.addItem(item2);
		order.addItem(item3);
		
		// Debe haber solo un item en la lista
		assertEquals(1, order.getItems().size());
		
		// La cantidad debe ser la suma de todas
		Item itemInOrder = order.getItems().iterator().next();
		assertEquals(10, itemInOrder.getQuantity()); // 2 + 3 + 5
	}
	
	// Métodos auxiliares para crear items mock
	private Item createMockItem(long productId, double price, int quantity) {
		Product product = new Product();
		product.setId(productId);
		return createMockItemWithProduct(product, price, quantity);
	}
	
	private Item createMockItemWithProduct(Product product, double price, int quantity) {
		return new Item() {
			private int currentQuantity = quantity;
			
			@Override
			public Product getProduct() {
				return product;
			}
			
			@Override
			public int getQuantity() {
				return currentQuantity;
			}
			
			@Override
			public void setQuantity(int newQuantity) {
				this.currentQuantity = newQuantity;
			}
			
			@Override
			public double getPrice() {
				return price;
			}
		};
	}

}
