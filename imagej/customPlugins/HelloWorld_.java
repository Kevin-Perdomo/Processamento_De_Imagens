import ij.IJ;
import ij.plugin.PlugIn;import ij.plugin.PlugIn;

public class HelloWorld_ implements PlugIn {
   public HelloWorld_() {
   }

   public void run(String arg) {
      IJ.error("Meu Primeiro Plugin customizado");
   }
}