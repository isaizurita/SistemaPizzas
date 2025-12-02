import os

# Ruta al directorio donde está tu proyecto
directorio = "/Users/isaizurita/PatronesdeDiseñodeSoftware/proyectoFinal"  # Ruta correcta
archivo_salida = "proyecto_completo.java"  # Archivo de salida

# Abrimos el archivo donde se combinará el código
with open(archivo_salida, "w") as salida:
    for subdir, dirs, files in os.walk(directorio):
        for file in files:
            if file.endswith(".java"):  # Solo archivos .java
                with open(os.path.join(subdir, file), "r") as f:
                    salida.write(f.read() + "\n\n")  # Escribe el código de cada archivo y agrega un salto de línea

