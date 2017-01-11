package ravensproject.VisualSolver;

public class Pixel {
    private final int x;
    private final int y;

    public Pixel(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof Pixel)) return false;

        Pixel pixel = (Pixel)obj;
        return x == pixel.x && y == pixel.y;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + x;
        hashCode = 31 * hashCode + y;
        return hashCode;
    }

    public static boolean IsDark(int pixelValue) {
        int red = (pixelValue >> 16) & 0xff;
        int green = (pixelValue >> 8) & 0xff;
        int blue = (pixelValue) & 0xff;

        return red < 255 && green < 255 && blue < 255;
    }
}
