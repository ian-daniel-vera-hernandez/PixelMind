    <?php

    header("Access-Control-Allow-Origin: *");
    header("Content-Type: application/json; charset=UTF-8");

    $servername = "localhost";
    $username = "root";
    $password = "";
    $dbname = "registros";

    $conn = new mysqli($servername, $username, $password, $dbname);

    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }

    $postData = json_decode(file_get_contents('php://input'), true);

    $usuario_id = $postData['usuario_id'] ?? 0;
    $nombre = $postData['nombre_completo'] ?? '';
    $correo = $postData['correo'] ?? '';

    $response = array();

    if (!empty($usuario_id) && !empty($nombre) && !empty($correo)) {

        $stmt = $conn->prepare("INSERT INTO perfiles (usuario_id, nombre_completo, correo) VALUES (?, ?, ?)");
        
        // "iss" significa: Integer (usuario_id), String (nombre), String (correo)
        $stmt->bind_param("iss", $usuario_id, $nombre, $correo);

        if ($stmt->execute()) {
            $response['status'] = 'success';
            $response['message'] = 'Perfil guardado correctamente.';
        } else {
            $response['status'] = 'error';
            $response['message'] = 'Error al guardar el perfil: ' . $stmt->error;
        }
        $stmt->close();

    } else {
        $response['status'] = 'error';
        $response['message'] = 'Faltan datos para guardar el perfil.';
    }

    $conn->close();

    echo json_encode($response);
    ?>
    
    