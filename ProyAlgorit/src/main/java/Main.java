public class Main {
    public static void main() {
        Login login = new Login();
        if(login.validar("Juan","12345"))
            System.out.println("Inicio de sesion exitoso");
        else
            System.out.println("Error...");
    }
}