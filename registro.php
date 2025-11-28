<?php

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");

$servername = "127.0.0.1"; // O la IP de tu servidor de base de datos
$username = "root";
$password = "";
$dbname = "registros";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    // Si falla la conexión, termina el script y muestra un error.
    die("Connection failed: " . $conn->connect_error);
}


$postData = json_decode(file_get_contents('php://input'), true);

$usuario = $postData['usuario'] ?? '';
$correo = $postData['correo'] ?? '';
$pass = $postData['password'] ?? '';


$response = array();

if (!empty($usuario) && !empty($correo) && !empty($pass)) {

    
    $password_hashed = password_hash($pass, PASSWORD_DEFAULT);

    $stmt = $conn->prepare("INSERT INTO usuarios (usuario, correo, password) VALUES (?, ?, ?)");
    $stmt->bind_param("sss", $usuario, $correo, $password_hashed);

    if ($stmt->execute()) {
        $response['status'] = 'success';
        $response['message'] = 'Usuario registrado correctamente.';
        $response['usuario_id'] = $conn->insert_id;
    } else {
        $response['status'] = 'error';

        if ($conn->errno == 1062) {
             $response['message'] = 'El nombre de usuario o el correo ya existen.';
        } else {
             $response['message'] = 'Error al registrar: ' . $stmt->error;
        }
    }

    $stmt->close();

} else {
    $response['status'] = 'error';
    $response['message'] = 'Todos los campos son obligatorios.';
}

$conn->close();

echo json_encode($response); [3, 4]
?>
