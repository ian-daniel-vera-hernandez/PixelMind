<?php
// 1. Encabezados para permitir conexión desde Androidheader("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");

// 2. Ocultar errores visuales de PHP para no ensuciar el JSON
error_reporting(0);
ini_set('display_errors', 0);

// 3. Configuración de la BD
$servername = "localhost";
$username = "root";
$password = ""; // Si tienes contraseña en root, ponla aquí
$dbname = "registros"; 

// 4. Conexión
$conn = new mysqli($servername, $username, $password, $dbname);

// Verificar conexión
if ($conn->connect_error) {
    http_response_code(500); // Código de error servidor
    echo json_encode(array("status" => "error", "message" => "Fallo conexion DB: " . $conn->connect_error));
    exit();
}

// 5. Recibir datos del JSON de Android
$postData = json_decode(file_get_contents('php://input'), true);

$correo = $postData['correo'] ?? '';
$passwordIngresada = $postData['password'] ?? '';

if (!empty($correo) && !empty($passwordIngresada)) {

    // 6. Consulta SQL (USANDO 'usuario' PORQUE ASÍ SE LLAMA EN TU TABLA)
    // IMPORTANTE: Consultamos también el password real para compararlo
    $stmt = $conn->prepare("SELECT id, usuario, password FROM usuarios WHERE correo = ?");
    
    if(!$stmt) {
        echo json_encode(array("status" => "error", "message" => "Error en consulta SQL: " . $conn->error));
        exit();
    }

    $stmt->bind_param("s", $correo);
    $stmt->execute();
    $stmt->store_result();

    if ($stmt->num_rows > 0) {
        $stmt->bind_result($id, $usuarioNombre, $passwordRealEnBD);
        $stmt->fetch();

        // 7. VERIFICACIÓN DE CONTRASEÑA
        // Opción A: Si guardaste la contraseña tal cual (texto plano)
        if ($passwordIngresada == $passwordRealEnBD) {
             echo json_encode(array(
                "status" => "success",
                "message" => $usuarioNombre,
                "usuarioId" => $id
            ));
        } 
        // Opción B: Si usaste password_hash() en el registro, usa esto en su lugar:
        // if (password_verify($passwordIngresada, $passwordRealEnBD)) { ... }
        else {
             echo json_encode(array("status" => "error", "message" => "Contraseña incorrecta"));
        }

    } else {
        echo json_encode(array("status" => "error", "message" => "Usuario no encontrado"));
    }
    $stmt->close();
} else {
    echo json_encode(array("status" => "error", "message" => "Faltan datos (correo o pass)"));
}

$conn->close();
?>
