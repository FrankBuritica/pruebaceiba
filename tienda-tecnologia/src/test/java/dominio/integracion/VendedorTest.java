package dominio.integracion;

import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import dominio.Vendedor;
import dominio.GarantiaExtendida;
import dominio.Producto;
import dominio.excepcion.GarantiaExtendidaException;
import dominio.repositorio.RepositorioProducto;
import dominio.repositorio.RepositorioGarantiaExtendida;
import persistencia.sistema.SistemaDePersistencia;
import testdatabuilder.ProductoTestDataBuilder;
import testdatabuilder.ProductoTestDataBuilderCienDias;
import testdatabuilder.ProductoTestDataBuilderDocientosDias;
import testdatabuilder.ProductotestDataBuilderVowels;

public class VendedorTest {

	private static final String COMPUTADOR_LENOVO = "Computador Lenovo";
	private static final String NOMBRE_CLIENTE = "Franki Buritica";
	private static final double PRECIO_CIEN_DIAS = 48000.00;
	private static final double PRECIO_DOCIENTOS_DIAS = 130000.00;
	private static final String FECHA_FINAL_CIEN_DIAS = "28/10/2019";
	private static final String FECHA_FIN_DOCIENTOS_DIAS_SIN_LUNES = "02/04/2019";
	private static final Date FECHA_ACTUAL = new Date();
	private static final int NUMERO_DIA_NO_HABIL = 0;
	
	
	private SistemaDePersistencia sistemaPersistencia;
	
	private RepositorioProducto repositorioProducto;
	private RepositorioGarantiaExtendida repositorioGarantia;

	@Before
	public void setUp() {
		
		sistemaPersistencia = new SistemaDePersistencia();
		
		repositorioProducto = sistemaPersistencia.obtenerRepositorioProductos();
		repositorioGarantia = sistemaPersistencia.obtenerRepositorioGarantia();
		
		sistemaPersistencia.iniciar();
	}
	

	@After
	public void tearDown() {
		sistemaPersistencia.terminar();
	}

	@Test
	public void generarGarantiaTest() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conNombre(COMPUTADOR_LENOVO).build();
		repositorioProducto.agregar(producto);
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE, FECHA_ACTUAL);

		// assert
		Assert.assertTrue(vendedor.tieneGarantia(producto.getCodigo()));
		Assert.assertNotNull(repositorioGarantia.obtenerProductoConGarantiaPorCodigo(producto.getCodigo()));

	}
	
	@Test
	public void validarVocalesCodigoProductoTest() {
		// arrange
		Producto producto = new ProductotestDataBuilderVowels().conNombre(COMPUTADOR_LENOVO).build();
		repositorioProducto.agregar(producto);
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);
		
		// assert
		try {
			vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE, FECHA_ACTUAL);
		} catch (GarantiaExtendidaException e) {
			Assert.assertEquals(Vendedor.EL_PRODUCTO_NO_TIENE_GARANTIA, e.getMessage());
		}
	}

	@Test
	public void productoYaTieneGarantiaTest() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conNombre(COMPUTADOR_LENOVO).build();
		
		repositorioProducto.agregar(producto);
		
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE, FECHA_ACTUAL);
		try {
			
			vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE, FECHA_ACTUAL);
			fail();
			
		} catch (GarantiaExtendidaException e) {
			// assert
			Assert.assertEquals(Vendedor.EL_PRODUCTO_TIENE_GARANTIA, e.getMessage());
		}
	}
	
	@Test
	public void garantiaExtendiaCienDias() {
		
		// arrange
		Producto producto = new ProductoTestDataBuilderCienDias().conNombre(COMPUTADOR_LENOVO).build();
		
		repositorioProducto.agregar(producto);
		
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE, FECHA_ACTUAL);
		
		//assert
		GarantiaExtendida garantia = repositorioGarantia.obtener(producto.getCodigo());
	    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
	    String strDate = formatter.format(garantia.getFechaFinGarantia());
		
		Assert.assertEquals(PRECIO_CIEN_DIAS, garantia.getPrecioGarantia(), 0.01);
		Assert.assertEquals(FECHA_FINAL_CIEN_DIAS, strDate);
		
	}
	
	@Test
	public void garantiaExtendiaDocientosDiasDiaHabil() {
		
		// arrange
		Producto producto = new ProductoTestDataBuilderDocientosDias().conNombre(COMPUTADOR_LENOVO).build();
		
		repositorioProducto.agregar(producto);
		
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE, FECHA_ACTUAL);
		
		//assert
		GarantiaExtendida garantia = repositorioGarantia.obtener(producto.getCodigo());
	    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
	    String strDate = formatter.format(garantia.getFechaFinGarantia());
		
		Assert.assertNotEquals(NUMERO_DIA_NO_HABIL, garantia.getFechaFinGarantia().getDay());
		
	}
	
	@Test
	public void garantiaExtendiaDocientosDiasSinLunes() throws ParseException {
		
		// arrange
		Producto producto = new ProductoTestDataBuilderDocientosDias().conNombre(COMPUTADOR_LENOVO).build();
		
		repositorioProducto.agregar(producto);
		
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateInString = "2018-08-16";
		Date fecha = sdf.parse(dateInString);
		
		vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE, fecha);
		
		//assert
		GarantiaExtendida garantia = repositorioGarantia.obtener(producto.getCodigo());
	    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
	    String strDate = formatter.format(garantia.getFechaFinGarantia());
		
		Assert.assertEquals(PRECIO_DOCIENTOS_DIAS, garantia.getPrecioGarantia(), 0.01);
		Assert.assertEquals(FECHA_FIN_DOCIENTOS_DIAS_SIN_LUNES, strDate);
		Assert.assertNotEquals(NUMERO_DIA_NO_HABIL, garantia.getFechaFinGarantia().getDay());
		
	}
}
