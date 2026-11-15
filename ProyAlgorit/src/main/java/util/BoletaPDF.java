package util;

import model.DetallePedido;
import model.Pedido;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.Deflater;

/**
 * Genera la boleta de un pedido en formato PDF.
 * No usa librerías externas: escribe el PDF directamente (fuentes Helvetica estándar).
 * Cambia las constantes de "DATOS DEL NEGOCIO" para personalizar la boleta.
 */
public final class BoletaPDF {

    // ---------- DATOS DEL NEGOCIO (edítalos) ---------------------------------------------
    private static final String NEGOCIO   = "El Cordón y la Rosa";
    private static final String RUC       = "RUC: 00000000000";
    private static final String DIRECCION = "Ica, Perú";
    private static final String SERIE     = "B001";
    private static final double IGV       = 0.18;   // los precios de la carta ya incluyen IGV

    // ---------- Página (A5 de ancho; el alto crece según los platos) ---------------------
    private static final float ANCHO = 420f;
    private static final float MARGEN = 36f;
    private static final float[] OLIVA = {0x5B / 255f, 0x6A / 255f, 0x33 / 255f};
    private static final float[] TINTA = {0x23 / 255f, 0x29 / 255f, 0x1B / 255f};
    private static final float[] GRIS  = {0x6C / 255f, 0x71 / 255f, 0x63 / 255f};
    private static final Charset WIN = Charset.forName("windows-1252");

    private BoletaPDF() {
    }

    public static String numeroBoleta(int idPedido) {
        return String.format("%s-%06d", SERIE, idPedido);
    }

    public static void generar(Pedido pedido, List<DetallePedido> detalles, File destino) throws IOException {
        byte[] pdf = construir(pedido, detalles);
        Files.write(destino.toPath(), pdf);
    }

    // ================================================================================
    //  Construcción del contenido
    // ================================================================================
    private static byte[] construir(Pedido pedido, List<DetallePedido> detalles) throws IOException {
        List<String[]> filas = new ArrayList<>();
        double total = 0;
        for (DetallePedido d : detalles) {
            filas.add(new String[]{
                    String.valueOf(d.getCantidad()),
                    d.getNombrePlato() == null ? "(plato eliminado)" : d.getNombrePlato(),
                    dinero(d.getPrecioUnitario()),
                    dinero(d.getSubtotal())});
            total += d.getSubtotal();
        }
        double base = total / (1 + IGV);
        double igv = total - base;

        float alto = Math.max(595f, 330f + filas.size() * 20f + 190f);
        Contenido c = new Contenido(alto);
        float y = alto - MARGEN;

        // Logo + datos del negocio
        BufferedImage logo = cargarLogo();
        float xTexto = MARGEN;
        if (logo != null) {
            float alLogo = 70f;
            float anLogo = alLogo * logo.getWidth() / logo.getHeight();
            c.imagen(MARGEN, y - alLogo, anLogo, alLogo);
            xTexto = MARGEN + anLogo + 14f;
        }
        c.texto(xTexto, y - 22, "F2", 13, OLIVA, NEGOCIO);
        c.texto(xTexto, y - 38, "F1", 9, GRIS, DIRECCION);
        c.texto(xTexto, y - 51, "F1", 9, GRIS, "Restaurante");

        // Recuadro con RUC y número de boleta
        float bx = ANCHO - MARGEN - 130f;
        c.rectBorde(bx, y - 70, 130f, 70f, OLIVA, 1.2f);
        c.textoCentrado(bx, 130f, y - 20, "F2", 9.5f, TINTA, RUC);
        c.textoCentrado(bx, 130f, y - 38, "F2", 10.5f, OLIVA, "BOLETA DE VENTA");
        c.textoCentrado(bx, 130f, y - 56, "F2", 10.5f, TINTA, numeroBoleta(pedido.getIdPedido()));
        y -= 70f + 24f;

        // Datos del pedido
        c.linea(MARGEN, y, ANCHO - MARGEN, y, new float[]{0.80f, 0.82f, 0.75f}, 0.8f);
        y -= 18f;
        String[][] datos = {
                {"Fecha y hora", valor(pedido.getFechaHora())},
                {"Cliente / DNI", valor(pedido.getDni())},
                {"Mesa", String.valueOf(pedido.getNumMesa())},
                {"Método de pago", valor(pedido.getMetodoPago())}
        };
        for (String[] par : datos) {
            c.texto(MARGEN, y, "F1", 9.5f, GRIS, par[0]);
            c.texto(MARGEN + 100f, y, "F2", 9.5f, TINTA, par[1]);
            y -= 15f;
        }
        y -= 8f;

        // Cabecera de la tabla de platos
        float xCant = MARGEN + 8f;
        float xDesc = MARGEN + 50f;
        float xPrecioDer = ANCHO - MARGEN - 80f;
        float xSubDer = ANCHO - MARGEN - 8f;
        c.rellenoRect(MARGEN, y - 6f, ANCHO - 2 * MARGEN, 22f, OLIVA);
        float[] blanco = {1f, 1f, 1f};
        c.texto(xCant, y + 1f, "F2", 9, blanco, "CANT.");
        c.texto(xDesc, y + 1f, "F2", 9, blanco, "DESCRIPCIÓN");
        c.textoDerecha(xPrecioDer, y + 1f, "F2", 9, blanco, "P. UNIT.");
        c.textoDerecha(xSubDer, y + 1f, "F2", 9, blanco, "IMPORTE");
        y -= 24f;

        // Filas
        int i = 0;
        for (String[] f : filas) {
            if (i % 2 == 1) {
                c.rellenoRect(MARGEN, y - 6f, ANCHO - 2 * MARGEN, 20f, new float[]{0.96f, 0.96f, 0.93f});
            }
            c.texto(xCant + 8f, y, "F1", 10, TINTA, f[0]);
            c.texto(xDesc, y, "F1", 10, TINTA, recortar(f[1], 34));
            c.textoDerecha(xPrecioDer, y, "F1", 10, TINTA, f[2]);
            c.textoDerecha(xSubDer, y, "F1", 10, TINTA, f[3]);
            y -= 20f;
            i++;
        }
        c.linea(MARGEN, y + 8f, ANCHO - MARGEN, y + 8f, new float[]{0.80f, 0.82f, 0.75f}, 0.8f);
        y -= 12f;

        // Totales
        float xEtiq = ANCHO - MARGEN - 170f;
        c.texto(xEtiq, y, "F1", 10, GRIS, "Op. gravada");
        c.textoDerecha(xSubDer, y, "F1", 10, TINTA, "S/ " + dinero(base));
        y -= 16f;
        c.texto(xEtiq, y, "F1", 10, GRIS, "IGV (" + (int) Math.round(IGV * 100) + "%)");
        c.textoDerecha(xSubDer, y, "F1", 10, TINTA, "S/ " + dinero(igv));
        y -= 8f;
        c.rellenoRect(xEtiq - 8f, y - 24f, ANCHO - MARGEN - xEtiq + 8f, 26f, OLIVA);
        c.texto(xEtiq, y - 16f, "F2", 12, blanco, "TOTAL");
        c.textoDerecha(xSubDer, y - 16f, "F2", 12, blanco, "S/ " + dinero(total));
        y -= 52f;

        c.texto(MARGEN, y, "F1", 9, GRIS, "Son: " + totalEnLetras(total));
        y -= 40f;

        // Pie
        c.textoCentrado(0, ANCHO, y, "F2", 11, OLIVA, "¡Gracias por su preferencia!");
        c.textoCentrado(0, ANCHO, y - 14f, "F1", 8.5f, GRIS, "Representación impresa de la boleta de venta");

        return new Documento(c, alto, logo).bytes();
    }

    // ================================================================================
    //  Utilidades de texto
    // ================================================================================
    private static String dinero(double v) {
        return String.format(Locale.US, "%.2f", v);
    }

    private static String valor(String s) {
        return s == null || s.isBlank() ? "-" : s;
    }

    private static String recortar(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    private static String totalEnLetras(double total) {
        long soles = (long) Math.floor(total + 1e-9);
        long centimos = Math.round((total - soles) * 100);
        if (centimos == 100) {
            soles++;
            centimos = 0;
        }
        return enLetras(soles).toUpperCase(Locale.ROOT) + String.format(" CON %02d/100 SOLES", centimos);
    }

    private static final String[] UNIDADES = {"cero", "uno", "dos", "tres", "cuatro", "cinco", "seis", "siete",
            "ocho", "nueve", "diez", "once", "doce", "trece", "catorce", "quince", "dieciséis", "diecisiete",
            "dieciocho", "diecinueve", "veinte", "veintiuno", "veintidós", "veintitrés", "veinticuatro",
            "veinticinco", "veintiséis", "veintisiete", "veintiocho", "veintinueve"};
    private static final String[] DECENAS = {"", "", "", "treinta", "cuarenta", "cincuenta", "sesenta",
            "setenta", "ochenta", "noventa"};
    private static final String[] CENTENAS = {"", "ciento", "doscientos", "trescientos", "cuatrocientos",
            "quinientos", "seiscientos", "setecientos", "ochocientos", "novecientos"};

    private static String enLetras(long n) {
        if (n < 30) {
            return UNIDADES[(int) n];
        }
        if (n < 100) {
            String d = DECENAS[(int) (n / 10)];
            return n % 10 == 0 ? d : d + " y " + UNIDADES[(int) (n % 10)];
        }
        if (n == 100) {
            return "cien";
        }
        if (n < 1000) {
            String r = n % 100 == 0 ? "" : " " + enLetras(n % 100);
            return CENTENAS[(int) (n / 100)] + r;
        }
        if (n < 1_000_000) {
            long miles = n / 1000;
            String pref = miles == 1 ? "mil" : enLetras(miles).replaceAll("uno$", "un") + " mil";
            return n % 1000 == 0 ? pref : pref + " " + enLetras(n % 1000);
        }
        return String.valueOf(n);
    }

    private static BufferedImage cargarLogo() {
        try (InputStream in = BoletaPDF.class.getResourceAsStream("/logo.png")) {
            if (in == null) {
                return null;
            }
            BufferedImage original = ImageIO.read(in);
            if (original == null) {
                return null;
            }
            int an = 150;
            int al = Math.round(original.getHeight() * (an / (float) original.getWidth()));
            BufferedImage reducida = new BufferedImage(an, al, BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics2D g = reducida.createGraphics();
            g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                    java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(original.getScaledInstance(an, al, Image.SCALE_SMOOTH), 0, 0, null);
            g.dispose();
            return reducida;
        } catch (IOException ex) {
            return null;
        }
    }

    // ================================================================================
    //  Flujo de contenido de la página
    // ================================================================================
    private static final class Contenido {
        private final StringBuilder sb = new StringBuilder();

        Contenido(float alto) {
        }

        private static String col(float[] c) {
            return String.format(Locale.US, "%.3f %.3f %.3f", c[0], c[1], c[2]);
        }

        private static String esc(String s) {
            StringBuilder o = new StringBuilder();
            for (char ch : s.toCharArray()) {
                if (ch == '(' || ch == ')' || ch == '\\') {
                    o.append('\\').append(ch);
                } else if (ch < 0x80) {
                    o.append(ch);
                } else {
                    byte[] b = String.valueOf(ch).getBytes(WIN);
                    o.append(String.format("\\%03o", b[0] & 0xFF));
                }
            }
            return o.toString();
        }

        /** Ancho aproximado usando las métricas reales de Helvetica. */
        static float ancho(String s, String fuente, float size) {
            boolean bold = "F2".equals(fuente);
            float w = 0;
            for (char ch : s.toCharArray()) {
                w += anchoCaracter(ch, bold);
            }
            return w * size / 1000f;
        }

        private static float anchoCaracter(char ch, boolean bold) {
            if (ch == ' ' || ch == '.' || ch == ',' || ch == ':' || ch == ';' || ch == '!' || ch == 'i'
                    || ch == 'l' || ch == 'j' || ch == '\'' || ch == '|') {
                return ch == ' ' || ch == '.' || ch == ',' ? 278 : (bold ? 278 : 222);
            }
            if (ch == '(' || ch == ')' || ch == 'f' || ch == 't' || ch == '/' || ch == '-') {
                return 333;
            }
            if (ch == 'm' || ch == 'M' || ch == 'W') {
                return ch == 'm' ? 833 : (ch == 'M' ? 833 : 944);
            }
            if (ch == 'w') {
                return 722;
            }
            if (Character.isDigit(ch)) {
                return 556;
            }
            if (Character.isUpperCase(ch)) {
                return bold ? 722 : 680;
            }
            return bold ? 590 : 540;
        }

        void texto(float x, float y, String f, float size, float[] color, String s) {
            sb.append("BT ").append(col(color)).append(" rg /").append(f).append(' ')
                    .append(String.format(Locale.US, "%.1f", size)).append(" Tf ")
                    .append(String.format(Locale.US, "%.2f %.2f", x, y)).append(" Td (")
                    .append(esc(s)).append(") Tj ET\n");
        }

        void textoDerecha(float xDer, float y, String f, float size, float[] color, String s) {
            texto(xDer - ancho(s, f, size), y, f, size, color, s);
        }

        void textoCentrado(float x, float w, float y, String f, float size, float[] color, String s) {
            texto(x + (w - ancho(s, f, size)) / 2f, y, f, size, color, s);
        }

        void linea(float x1, float y1, float x2, float y2, float[] color, float grosor) {
            sb.append(col(color)).append(" RG ").append(String.format(Locale.US, "%.2f w %.2f %.2f m %.2f %.2f l S\n",
                    grosor, x1, y1, x2, y2));
        }

        void rectBorde(float x, float y, float w, float h, float[] color, float grosor) {
            sb.append(col(color)).append(" RG ").append(String.format(Locale.US,
                    "%.2f w %.2f %.2f %.2f %.2f re S\n", grosor, x, y, w, h));
        }

        void rellenoRect(float x, float y, float w, float h, float[] color) {
            sb.append(col(color)).append(" rg ").append(String.format(Locale.US,
                    "%.2f %.2f %.2f %.2f re f\n", x, y, w, h));
        }

        void imagen(float x, float y, float w, float h) {
            sb.append("q ").append(String.format(Locale.US, "%.2f 0 0 %.2f %.2f %.2f cm /Im1 Do Q\n", w, h, x, y));
        }

        byte[] bytes() {
            return sb.toString().getBytes(WIN);
        }
    }

    // ================================================================================
    //  Estructura del archivo PDF
    // ================================================================================
    private static final class Documento {
        private final ByteArrayOutputStream out = new ByteArrayOutputStream();
        private final List<Integer> offsets = new ArrayList<>();

        Documento(Contenido c, float alto, BufferedImage logo) throws IOException {
            escribir("%PDF-1.4\n");
            int idImg = 6;
            int idImgMask = 7;

            objeto(1, "<< /Type /Catalog /Pages 2 0 R >>");
            objeto(2, "<< /Type /Pages /Kids [3 0 R] /Count 1 >>");
            objeto(3, String.format(Locale.US,
                    "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 %.2f %.2f] /Contents 4 0 R "
                            + "/Resources << /Font << /F1 5 0 R /F2 8 0 R >>%s >> >>",
                    ANCHO, alto, logo == null ? "" : " /XObject << /Im1 " + idImg + " 0 R >>"));
            flujo(4, "", c.bytes());
            objeto(5, "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>");

            if (logo != null) {
                int an = logo.getWidth();
                int al = logo.getHeight();
                byte[] rgb = new byte[an * al * 3];
                byte[] alfa = new byte[an * al];
                int p = 0;
                int a = 0;
                for (int yy = 0; yy < al; yy++) {
                    for (int xx = 0; xx < an; xx++) {
                        int argb = logo.getRGB(xx, yy);
                        int al8 = (argb >>> 24) & 0xFF;
                        rgb[p++] = (byte) ((argb >> 16) & 0xFF);
                        rgb[p++] = (byte) ((argb >> 8) & 0xFF);
                        rgb[p++] = (byte) (argb & 0xFF);
                        alfa[a++] = (byte) al8;
                    }
                }
                flujo(idImg, "/Type /XObject /Subtype /Image /Width " + an + " /Height " + al
                        + " /ColorSpace /DeviceRGB /BitsPerComponent 8 /SMask " + idImgMask + " 0 R /Filter /FlateDecode",
                        comprimir(rgb));
                flujo(idImgMask, "/Type /XObject /Subtype /Image /Width " + an + " /Height " + al
                        + " /ColorSpace /DeviceGray /BitsPerComponent 8 /Filter /FlateDecode", comprimir(alfa));
            } else {
                objeto(idImg, "null");
                objeto(idImgMask, "null");
            }
            objeto(8, "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold /Encoding /WinAnsiEncoding >>");

            int xref = out.size();
            escribir("xref\n0 " + (offsets.size() + 1) + "\n0000000000 65535 f \n");
            for (int off : offsets) {
                escribir(String.format("%010d 00000 n \n", off));
            }
            escribir("trailer\n<< /Size " + (offsets.size() + 1) + " /Root 1 0 R >>\nstartxref\n" + xref + "\n%%EOF\n");
        }

        byte[] bytes() {
            return out.toByteArray();
        }

        private void escribir(String s) throws IOException {
            out.write(s.getBytes(WIN));
        }

        private void objeto(int id, String cuerpo) throws IOException {
            registrar(id);
            escribir(id + " 0 obj\n" + cuerpo + "\nendobj\n");
        }

        private void flujo(int id, String dict, byte[] datos) throws IOException {
            registrar(id);
            escribir(id + " 0 obj\n<< " + dict + " /Length " + datos.length + " >>\nstream\n");
            out.write(datos);
            escribir("\nendstream\nendobj\n");
        }

        private void registrar(int id) {
            // Los objetos se escriben en orden creciente salvo el 8, que va al final: se corrige abajo.
            while (offsets.size() < id) {
                offsets.add(0);
            }
            offsets.set(id - 1, out.size());
        }

        private static byte[] comprimir(byte[] datos) {
            Deflater def = new Deflater(Deflater.BEST_COMPRESSION);
            def.setInput(datos);
            def.finish();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            while (!def.finished()) {
                int n = def.deflate(buf);
                bos.write(buf, 0, n);
            }
            def.end();
            return bos.toByteArray();
        }
    }
}
