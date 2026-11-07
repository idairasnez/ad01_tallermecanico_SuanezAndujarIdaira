package org.iesalandalus.programacion.tallermecanico.modelo.negocio.ficheros.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.iesalandalus.programacion.tallermecanico.modelo.TallerMecanicoExcepcion;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Cliente;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.IClientes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Clientes implements IClientes {

    //Ruta del fichero JSON
    private static final String FICHERO_CLIENTES = String.format("%s%s%s%s%s%s%s", "datos", File.separator, "ficheros", File.separator, "json", File.separator, "clientes.json");
    private static Clientes instancia;
    private final ObjectMapper mapper;

    private Clientes() {
        mapper = new ObjectMapper();
    }

    static Clientes getInstancia() {
        if (instancia == null) {
            instancia = new Clientes();
        }
        return instancia;
    }

    @Override
    public void comenzar() {
        // Vacío: los datos se leen cuando el usuario los solicita.
    }

    @Override
    public void terminar() {
        // Vacío: los datos se escriben cada vez que hay una modificación.
    }

    //Deserializamos (leemos) el fichero de clientes
    private List<Cliente> leer() {
        File fichero = new File(FICHERO_CLIENTES);
        List<Cliente> clientes = new ArrayList<>();
        if (fichero.exists()) {
            try {
                clientes = mapper.readValue(fichero, new TypeReference<List<Cliente>>() {});
            } catch (IOException e) {
                System.out.printf("Error al leer el fichero %s: %s%n", FICHERO_CLIENTES, e.getMessage());
            }
        }
        return clientes;
    }

    //Serializamos (escribimos) el fichero
    private void escribir(List<Cliente> clientes) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FICHERO_CLIENTES), clientes);
        } catch (IOException e) {
            System.out.printf("Error al escribir el fichero %s: %s%n", FICHERO_CLIENTES, e.getMessage());
        }
    }

    @Override
    public List<Cliente> get() {
        return leer();
    }

    //Insertar clientes
    @Override
    public void insertar(Cliente cliente) throws TallerMecanicoExcepcion {
        Objects.requireNonNull(cliente, "No se puede insertar un cliente nulo.");
        List<Cliente> clientes = leer();
        if (clientes.contains(cliente)) {
            throw new TallerMecanicoExcepcion("Ya existe un cliente con ese DNI.");
        }
        clientes.add(cliente);
        escribir(clientes);
    }

    //Modificamos clientes
    @Override
    public Cliente modificar(Cliente cliente, String nombre, String telefono) throws TallerMecanicoExcepcion {
        Objects.requireNonNull(cliente, "No se puede modificar un cliente nulo.");
        List<Cliente> clientes = leer();
        int indice = clientes.indexOf(cliente);
        if (indice == -1) {
            throw new TallerMecanicoExcepcion("No existe ningún cliente con ese DNI.");
        }
        Cliente clienteEncontrado = clientes.get(indice);
        if (nombre != null && !nombre.isBlank()) {
            clienteEncontrado.setNombre(nombre);
        }
        if (telefono != null && !telefono.isBlank()) {
            clienteEncontrado.setTelefono(telefono);
        }
        escribir(clientes);
        return clienteEncontrado;
    }

    //Buscamos clientes
    @Override
    public Cliente buscar(Cliente cliente) {
        Objects.requireNonNull(cliente, "No se puede buscar un cliente nulo.");
        List<Cliente> clientes = leer();
        int indice = clientes.indexOf(cliente);
        return (indice == -1) ? null : clientes.get(indice);
    }

    //Borramos clientes
    @Override
    public void borrar(Cliente cliente) throws TallerMecanicoExcepcion {
        Objects.requireNonNull(cliente, "No se puede borrar un cliente nulo.");
        List<Cliente> clientes = leer();
        if (!clientes.contains(cliente)) {
            throw new TallerMecanicoExcepcion("No existe ningún cliente con ese DNI.");
        }
        clientes.remove(cliente);
        escribir(clientes);
    }
}



