package br.com.samuka.opencv.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.embed.swing.SwingFXUtils;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javax.imageio.ImageIO;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class TrabalhoFinal {

    public static final int RUIDO_CRUZ = 1;

    public final int IND_CIMA = 0;
    public final int IND_BAIXO = 1;
    public final int IND_ESQUERDA = 2;
    public final int IND_DIREITA = 3;

    int[][] sumXY = new int[][]{
        new int[]{0, -1},
        new int[]{0, +1},
        new int[]{-1, 0},
        new int[]{+1, 0},};

    private PixelReader pxReader;

    private PixelWriter pxWriter;

    public int percentualAjust;

    public Image ajusteInclinacaoImage(String imgUrl) {

        Image image = new Image("file:\\" + imgUrl);

        List<Integer> directions = new ArrayList<>();
        int horarioAntiHorario = 0;

        /*
		 * retorno[0] = pxReader.getColor(i, j - 1); retorno[1] = pxReader.getColor(i, j
		 * + 1); retorno[2] = pxReader.getColor(i - 1, j); retorno[3] =
		 * pxReader.getColor(i + 1, j);
         */
        pxReader = image.getPixelReader();
        WritableImage wImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        pxWriter = wImage.getPixelWriter();
        int i = 1;
        int j = 1;
        int count = 0;
        boolean back = false;
        while (i < image.getWidth() - 1) {
            j = 1;
            while (j < image.getHeight() - 1) {
                Color oldColor = pxReader.getColor(i, j);

                if (isWhite(oldColor)) {

                    Color[] colors = null;

                    colors = getCruz(pxReader, i, j);

                    if (isWhite(colors[IND_BAIXO])) {

                        directions.add(IND_BAIXO);

                        int x = i;
                        int y = j;
                        y++;

                        int[] xy = new int[]{-1, -1};

                        int lastDirectionX = IND_BAIXO;
                        int lastDirectionY = 0;

                        while (xy[0] != i || xy[1] != j) {
                            if (xy[0] == -1 && xy[1] == -1) {
                                xy[0] = i;
                                xy[1] = y;
                            }

                            xy = goToSquare(directions.get(directions.size() - 1), xy[0], xy[1], sumXY[directions.get(directions.size() - 1)][0], sumXY[directions.get(directions.size() - 1)][1], true);

                            int nextDirection = getNextDirection(directions.get(directions.size() - 1), xy[0], xy[1], directions.get(directions.size() - 1) > 1 ? lastDirectionY : lastDirectionX);

                            directions.add(nextDirection);
                            if (nextDirection > 1) {
                                lastDirectionX = nextDirection;
                            } else {
                                lastDirectionY = nextDirection;
                            }

                            /*if(directions.size() > 4) {
								back = true;
								break;
							}*/
                            //xy = goToSquare(xy[0], xy[1], sumXY[IND_BAIXO][0], sumXY[IND_BAIXO][1]);
                        }

                    }

                }
                j++;
                if (back) {
                    break;
                }
            }
            i++;
            if (back) {
                break;
            }
        }

        if (directions.size() > 4) {
            if (directions.contains(IND_BAIXO)
                    && directions.contains(IND_CIMA)
                    && directions.contains(IND_DIREITA)
                    && directions.contains(IND_ESQUERDA)) {
                percentualAjust = 0;
            } else {
                if (directions.contains(IND_DIREITA)) {
                    percentualAjust = -2;
                } else {
                    percentualAjust = 2;
                }
            }

        }

        return wImage;
    }

    private Color[] getCruz(PixelReader pxReader, int i, int j) {

        Color[] retorno = new Color[4];

        retorno[0] = pxReader.getColor(i, j - 1);
        retorno[1] = pxReader.getColor(i, j + 1);
        retorno[2] = pxReader.getColor(i - 1, j);
        retorno[3] = pxReader.getColor(i + 1, j);

        return retorno;

    }

    private boolean isWhite(Color color) {

        boolean retorno = false;
        //0.85
        double valueDefault = 0.70;

        if (color.getRed() > valueDefault && color.getGreen() > valueDefault && color.getBlue() > valueDefault) {
            retorno = true;
        }

        return retorno;

    }

    /**
     * Obt�m a proxima dire��o a ser seguida.
     *
     * @param directionAtual dire��o seguida atualmente.
     * @param type tipo da dire��o.
     * @return dire��o a ser seguida.
     */
    private int getNextDirection(int directionAtual, int x, int y, int directionAnterior) {

        int retorno = -1;

        Color[] colors = getCruz(pxReader, x, y);

        if (directionAnterior != -1) {
            if (isWhite(colors[directionAnterior])) {
                return directionAnterior;
            }
        }

        switch (directionAtual) {
            case IND_CIMA:

                retorno = isWhite(colors[IND_DIREITA]) ? IND_DIREITA : IND_DIREITA;
                break;
            case IND_BAIXO:
                retorno = isWhite(colors[IND_DIREITA]) ? IND_DIREITA : IND_DIREITA;
                break;
            case IND_DIREITA:
                retorno = isWhite(colors[IND_CIMA]) ? IND_CIMA : IND_BAIXO;
                break;
            case IND_ESQUERDA:
                retorno = isWhite(colors[IND_CIMA]) ? IND_CIMA : IND_BAIXO;
                break;
        }

        return retorno;
    }

    /**
     * Percorre uma direção do quadrado enquando a condição de verificação for
     * verdadeira.
     *
     * @param direction Direção a ser seguida.
     * @param x Posição x atual.
     * @param y Posição y atual.
     * @param sumX Valor de acréscimo ou decréscimo para a direção x.
     * @param sumY Valor de acréscimo ou decréscimo para a direção y.
     * @param write true para desenhar as coordenadas.
     * @return
     */
    private int[] goToSquare(int direction, int x, int y, int sumX, int sumY, boolean write) {

        Color[] colors = getCruz(pxReader, x, y);

        while (isWhite(colors[direction])) {
            x = x + sumX;
            y = y + sumY;
            colors = getCruz(pxReader, x, y);
            if (write) {
                pxWriter.setColor(x, y, new Color(0, 0, 0, 1));
            }
        }

        // x = x - sumX;
        // y = y - sumY;
        return new int[]{x, y};

    }

    /**
     * Obtém somente o jogo sudoku da imagem
     *
     * @param imgUrl Imagem a ser verificada.
     * @param pontos pontos da imagem a ser executado.
     * @param imageDest Imagem a ser copiada.
     * @return Imagem a ser salva.
     */
    public Image getSudokuImage(String imgUrl, ArrayList<Point[]> pontos, Image imageDest) {

        Image image = new Image("file:\\" + imgUrl);

        //Point p1, p2, p3, p4;
        int defaultPosX = 20;
        int defaultPosY = 20;

        List<Integer> directions = new ArrayList<>();

        pxReader = image.getPixelReader();
        Point[] ptInicial = null, ptFinal = null;

        if (pontos.size() >= 4) {
            /*
                temos o inicio do sudoku e o fim.
             */
            for (Point[] points : pontos) {

                if (ptInicial == null && ptFinal == null) {
                    ptInicial = points;
                    ptFinal = points;
                } else if (points[0].x < ptInicial[0].x) {
                    ptInicial = points;
                } else if (points[0].x > ptFinal[0].x) {
                    ptFinal = points;
                }
            }
        }

        int i = (int) ptInicial[0].x;
        int j = 1;
        boolean back = false;
        while (i < image.getWidth() - 1) {
            j = 1;
            while (j < image.getHeight() - 1) {
                Color oldColor = pxReader.getColor(i, j);

                if (isWhite(oldColor) && i >= defaultPosX && j >= defaultPosY) {
                    back = true;
                    break;
                }

                j++;
                if (back) {
                    break;
                }
            }
            i++;
            if (back) {
                break;
            }
        }

        ptInicial[0].y = j;
        ptFinal[0].y = j;

        int width = (int) (ptFinal[0].x - ptInicial[0].x);
        
        Point pt1 = new Point(ptInicial[0].x, ptInicial[0].y);
        int[] xy = goToSquare(IND_ESQUERDA, (int) pt1.x, (int) pt1.y, sumXY[IND_ESQUERDA][0], sumXY[IND_ESQUERDA][1], false);

        pt1 = new Point(xy[0], xy[1]);

        Point pt2 = new Point(ptFinal[0].x, ptFinal[0].y);

        xy = goToSquare(IND_DIREITA, (int) pt2.x, (int) pt2.y, sumXY[IND_DIREITA][0], sumXY[IND_DIREITA][1], false);

        pt2 = new Point(xy[0], xy[1]);

        width = (int) (pt2.x - pt1.x);

        Color color = pxReader.getColor((int) ptInicial[0].x, (int) ptInicial[0].y + width);

        int yToColor = (int) ptInicial[0].y + width;
        boolean asMovedY = false;
        while (!isWhite(color)) {
            yToColor--;
            color = pxReader.getColor((int) ptInicial[0].x, yToColor);
            asMovedY = true;
        }
        
        if(((int) ptInicial[0].y + width) - yToColor > 50) {
            //rotate
            return null;
        }
        

        Point pt3 = new Point(ptInicial[0].x, yToColor);

        Point pt4 = new Point(ptFinal[0].x, yToColor);

        int height = width;

        if (asMovedY) {
            height = width - Math.abs(((int) ptInicial[0].y + width) - yToColor);
        }

        double difPixAngulo = pt3.x - pt1.x;

        if (difPixAngulo > 3) {

        }
        
        pxReader = imageDest.getPixelReader();
        WritableImage wImage = new WritableImage(width, height);
        pxWriter = wImage.getPixelWriter();
        int xnew = 0;
        int ynew = 0;
        i = (int) pt1.x;
        while (i < pt2.x) {

            j = (int) pt1.y;
            ynew = 0;
            while (j < pt4.y) {

                Color oldColor = pxReader.getColor(i, j);
                //oldColor
                pxWriter.setColor(xnew, ynew, oldColor);

                j++;
                ynew++;
            }
            i++;
            xnew++;
        }

        return wImage;

    }

    public Image getFinalGame(String imgUrl, ArrayList<Point[]> pontos, Image imageDest) {

        Image image = new Image("file:\\" + imgUrl);

        int width = (int) (image.getWidth());// - 2);
        int height = (int) (image.getHeight());// - 2);

        int widthNode = width / 9;
        int heightNode = height / 9;

        pxReader = imageDest.getPixelReader();
        WritableImage wImageDest = new WritableImage(widthNode * 9, heightNode * 9);
        PixelWriter pxwImageDest = wImageDest.getPixelWriter();
        Color colorToPaint = new Color(0, 0, 0, 1);
        
        
        WritableImage wImage = new WritableImage(widthNode, heightNode);
        pxWriter = wImage.getPixelWriter();

        String path = MainController.absolutePath + "numerais\\";
        
        for (int i = 0; i <= 8; i++) {
            for (int j = 0; j <= 8; j++) {

                String name = i+"-"+j;
                
                getImageRowCol(pxReader, pxWriter, i, j, widthNode, heightNode, false, null);

                getCanny(wImage, path, 300, 100, 3, name);
                getDilatation(path + "canny" + name + ".jpg", path, name);

                Image imgVerify = new Image("file:\\" + path + "dilatacao" + name + ".jpg");

                PixelReader pxReaderVerify = imgVerify.getPixelReader();

                boolean isNumber = isNumber(pxReaderVerify, (int) imgVerify.getWidth(), (int) imgVerify.getHeight());

                if(!isNumber) {
                    getImageRowCol(pxReader, pxwImageDest, i, j, widthNode, heightNode, true, colorToPaint);
                }
                
            }
        }
        
        return wImageDest;

    }

    private void getImageRowCol(PixelReader reader, PixelWriter writer, int row, int col, int widthNode, int heightNode, boolean paintImage, Color defaultColor) {

        int x = col * widthNode;
        int y = row * heightNode;

        int xDest = x + widthNode;
        int yDest = y + heightNode;

        int xWrite = 0;
        int yWrite = 0;
        while (x < xDest) {
            y = row * heightNode;
            yWrite = 0;
            while (y < yDest) {

                Color oldColor = reader.getColor(x, y);
                if(paintImage) {
                    if(defaultColor != null) {
                        writer.setColor(x, y, defaultColor);
                    } else {
                        writer.setColor(x, y, oldColor);
                    }
                } else {
                    writer.setColor(xWrite, yWrite, oldColor);
                }
                

                y++;
                yWrite++;
            }
            x++;
            xWrite++;
        }

    }

    private boolean isNumber(PixelReader reader, int width, int height) {
        int x = 0;
        int y = 0;

        boolean isNumber = false;

        /*
            seto um valor para não identificar as bordas.
            Os números estão sempre no centro do quadrado.
         */
        int xActive = width / 6;
        int xActiveStart = xActive;
        int xActiveEnd = width - xActive;

        int yActive = height / 6;
        int yActiveStart = yActive;
        int yActiveEnd = height - yActive;

        int qtdWhite = 0;
        int qtdDark = 0;

        while (x < width) {
            y = 0;
            while (y < height) {

                Color oldColor = reader.getColor(x, y);

                if (x > xActiveStart && x < xActiveEnd
                        && y > yActiveStart && y < yActiveEnd) {
                    if (isWhite(oldColor)) {
                        qtdWhite++;
                    } else {
                        qtdDark++;
                    }
                }
                y++;
            }
            x++;
        }

        int totalPixelCount = qtdWhite + qtdDark;

        int thresholdWhite = (int) (totalPixelCount * 0.20);

        if (qtdWhite > thresholdWhite) {
            isNumber = true;
        }

        return isNumber;

    }

    public void getCanny(Image image, String path, double threshold1, double threshold2, int apertureSize, String optionalName) {

        if(optionalName == null) {
            optionalName = "";
        }
        
        Image imageGray = getImageGrayScale(image, 0, 0, 0);

        BufferedImage buffer = SwingFXUtils.fromFXImage(imageGray, null);

        String fileName = path + "gray" + optionalName + ".jpg";

        File file = new File(fileName);

        try {
            ImageIO.write(buffer, "jpg", file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Mat source = Imgcodecs.imread(file.getAbsolutePath(), Imgcodecs.CV_LOAD_IMAGE_COLOR);
        Mat destination = new Mat(source.rows(), source.cols(), source.type());

        //330, 100, 3
        Imgproc.Canny(source, destination, threshold1, threshold2, apertureSize, false);

        Imgcodecs.imwrite(path + "canny" + optionalName + ".jpg", destination);

        Image retorno = new Image("file:\\" + path + "canny" + optionalName + ".jpg");

    }

    public Image getDilatation(String urlImage, String dirSave, String optionalName) {

        if(optionalName == null) {
            optionalName = "";
        }
        
        //String path = MainController.absolutePath + MainController.ABSOLUTE_PATH_WORK;
        Mat source = Imgcodecs.imread(urlImage, Imgcodecs.CV_LOAD_IMAGE_COLOR);

        Mat destination = new Mat(source.rows(), source.cols(), source.type());
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(4, 4));

        Imgproc.dilate(source, destination, element);

        Imgcodecs.imwrite(dirSave + "dilatacao" + optionalName + ".jpg", destination);

        Image image = new Image("file:\\" + dirSave + "dilatacao" + optionalName + ".jpg");

        return image;

    }

    // fazer media aritmetica
    private Image getImageGrayScale(Image image, int r, int g, int b) {

        PixelReader pxReader = image.getPixelReader();
        WritableImage wImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        PixelWriter pxWriter = wImage.getPixelWriter();
        int i = 1;
        int j = 1;
        while (i < image.getWidth()) {
            j = 1;
            while (j < image.getHeight()) {
                Color oldColor = pxReader.getColor(i, j);
                double media = (oldColor.getRed() + oldColor.getBlue() + oldColor.getGreen()) / 3;

                // pedia ponderada
                // r*red + g*gren + b*blue / 100
                if (r != 0 && g != 0 && b != 0) {
                    media = (oldColor.getRed() * r + oldColor.getBlue() * b + oldColor.getGreen() * g) / 100;
                }

                pxWriter.setColor(i, j, new Color(media, media, media, oldColor.getOpacity()));
                j++;
            }
            i++;
        }

        return wImage;
    }

}
