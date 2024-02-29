package org.arep.taller4.mySpring.runtime;

import org.arep.taller4.copySpark.Request;
import org.arep.taller4.mySpring.anotation.GetMapping;
import org.arep.taller4.mySpring.anotation.PostMapping;
import org.arep.taller4.mySpring.anotation.RequestMapping;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * Clase que maneja los métodos que serán ejecutados cuando se realiza una petición HTTP
 */
public class ComponentLoader {
    private static final Map<String, Method> GET_SERVICES = new HashMap<>();
    private static final Map<String, Method> POST_SERVICES = new HashMap<>();


    private static final String PACKAGE_NAME = "org/arep/taller4/mySpring/controller";

    /**
     * Método que busca en el packete "org/arep/taller4/mySpring/controller" componentes de spring y lo guarda para luego
     * ser ejecutado
     * @throws ClassNotFoundException Se lanza cuando no encuentra una clase en el paquete especificado
     */
    public static void loadComponents() throws ClassNotFoundException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        URL packageURL = classLoader.getResource(PACKAGE_NAME);
        loadMethods(packageURL);
    }

    /**
     * Método que busca un método que puede hacer referencia a una petición get o post dependiendo del nombre (end point)
     * @param endpoint Nombre del endpoint del método
     * @param verb Verbo http que se decea buscar
     * @return Objeto Method que representa un método
     */
    public static Method search(String endpoint, String verb){
        switch (verb){
            case "GET":
                return GET_SERVICES.get(endpoint);
            case "POST":
                return POST_SERVICES.get(endpoint);
        }
        return null;
    }

    /**
     * Método que carga los métodos de un directorio y revisa sus componentes
     * @param packageURL URL de la ubicación de los archivos (clases)
     * @throws ClassNotFoundException Se lanza cuando no encuentra una clase en el paquete especificado
     */
    private static void loadMethods(URL packageURL) throws ClassNotFoundException {
        if (packageURL != null) {
            String packagePath = packageURL.getPath();
            if (packagePath != null) {
                File packageDir = new File(packagePath);
                if (packageDir.isDirectory()) {
                    File[] files = packageDir.listFiles();
                    assert files != null;
                    checkComponent(files);
                }
            }
        }
    }

    /**
     * Método que revisa si una clase tiene la notación @RequestMapping
     * @param files archivos que contiene un directorio
     * @throws ClassNotFoundException Se lanza cuando no encuentra una clase en el paquete especificado
     */
    private static void checkComponent(File[] files) throws ClassNotFoundException {
        for(File file : files){
            String className = file.getName();
            if(className.endsWith(".class")){
                System.out.println("Clase cargada: "+className);
                className = PACKAGE_NAME + "/" + className.substring(0,className.length() - 6);
                Class<?> clazz = Class.forName(className.replace("/","."));
                if(clazz.isAnnotationPresent(RequestMapping.class)){
                    saveComponentMethods(clazz);
                }
            }
        }
    }

    /**
     * Método que revisa si los métodos contienen las notaciones @GetMapping o @PostMapping y los agrega a un diccionario
     * de métodos
     * @param classes Clases que se desean revisar
     */
    private static void saveComponentMethods(Class<?> classes){
        Method[] methods = classes.getDeclaredMethods();
        for (Method m : methods){
            if(m.isAnnotationPresent(GetMapping.class)){

                String endPoint = m.getAnnotation(GetMapping.class).value();
                GET_SERVICES.put(endPoint,m);
            } else if (m.isAnnotationPresent(PostMapping.class)) {
                String endPoint = m.getAnnotation(PostMapping.class).value();
                POST_SERVICES.put(endPoint,m);
            }
        }
    }

    /**
     * Método que ejecuta un método con sus parámetros
     * @param method Método que se desea ejecutar
     * @param param Parámetros que necesita el método para ser ejecutado
     * @return La salida del método casteada como String
     * @throws InvocationTargetException Se lanza si falla la ejecución del método
     * @throws IllegalAccessException Se lanza si no se puede acceder al método
     */
    public static String execute(Method method, Request param) throws InvocationTargetException, IllegalAccessException {
        return (String) method.invoke(null, param);
    }

}
