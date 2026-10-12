package controller;

import dao.ConexionDB;
import dao.LoginDAO;

/** Valida el inicio de sesión; la vista ya no habla con el DAO. */
public class LoginController {

    public enum Resultado { OK, CREDENCIALES_INCORRECTAS, SIN_CONEXION }

    private final LoginDAO loginDAO = new LoginDAO();

    public Resultado iniciarSesion(String usuario, String contrasena) {
        if (!ConexionDB.probarConexion()) {
            return Resultado.SIN_CONEXION;
        }
        return loginDAO.validar(usuario, contrasena) ? Resultado.OK : Resultado.CREDENCIALES_INCORRECTAS;
    }
}
