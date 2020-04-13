package br.com.samuka.opencv.util;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class ImageManipule {

	public static final int RUIDO_CRUZ = 1;
	public static final int RUIDO_3X3 = 2;
	public static final int RUIDO_X = 3;

	public static final int RUIDO_MEDIA = 10;
	public static final int RUIDO_MEDIANA = 11;

	/**
	 * A Quantidade de tons � definida ap�s a execu��o do m�todo
	 * getHistograma
	 */
	private static int qtdTonsRed;
	private static int qtdTonsGreen;
	private static int qtdTonsBlue;
	
	// fazer media aritmetica
	public static Image getImageGrayScale(Image image, int r, int g, int b) {

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

	public static Image getImageNegativa(Image image) {

		PixelReader pxReader = image.getPixelReader();
		WritableImage wImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
		PixelWriter pxWriter = wImage.getPixelWriter();
		int i = 1;
		int j = 1;
		while (i < image.getWidth() - 1) {
			j = 1;
			while (j < image.getHeight() - 1) {
				Color oldColor = pxReader.getColor(i, j);
				pxWriter.setColor(i, j, new Color(1 - oldColor.getRed(), 1 - oldColor.getGreen(),
						1 - oldColor.getBlue(), oldColor.getOpacity()));
				j++;
			}
			i++;
		}

		// iv3.setImage(wImage);
		return wImage;
	}

	/**
	 * Aplica Limiariza��o.
	 * 
	 * @param image
	 * @param value
	 *            Valor entre 0 e 1.
	 * @return
	 */
	public static Image getImageLimiarizacao(Image image, double value, int zebrado, boolean isHorizontal) {
		System.out.println(image.getWidth() + " - " + zebrado);
		PixelReader pxReader = image.getPixelReader();
		WritableImage wImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
		PixelWriter pxWriter = wImage.getPixelWriter();
		int i = 1;
		int j = 1;
		double qtdDivisao = (zebrado != -1) ? image.getWidth() / zebrado : 0;
		System.out.println(qtdDivisao);
		double vlrAtual = qtdDivisao;
		boolean isLimiar = qtdDivisao > 0 ? false : true;
		while (i < image.getWidth()) {
			System.err.println("" + i);
			if (qtdDivisao != 0 && qtdDivisao < i) {
				System.err.println("troco limiar " + i);
				isLimiar = !isLimiar;
				qtdDivisao += vlrAtual;
				System.err.println("" + qtdDivisao);
			}
			j = 1;
			while (j < image.getHeight()) {
				Color oldColor = pxReader.getColor(i, j);

				if (isLimiar) {
					pxWriter.setColor(i, j,
							new Color(value >= oldColor.getRed() ? 0 : 1, value >= oldColor.getGreen() ? 0 : 1,
									value >= oldColor.getBlue() ? 0 : 1, oldColor.getOpacity()));
				} else {
					pxWriter.setColor(i, j, new Color(oldColor.getRed(), oldColor.getGreen(), oldColor.getBlue(),
							oldColor.getOpacity()));
				}

				j++;
			}
			i++;
		}

		return wImage;
	}

	/**
	 * Obtem a imagem sem ru�do.
	 * 
	 * @param image
	 *            Imagem a ser tratada
	 * @param metodo
	 *            m�todo a ser executado
	 * @param selecao
	 *            processo de sele��o
	 * @return Imagem sem ruido.
	 */
	public static Image getImagemSemRuido(Image image, int metodo, int selecao) {

		PixelReader pxReader = image.getPixelReader();
		WritableImage wImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
		PixelWriter pxWriter = wImage.getPixelWriter();
		int i = 1;
		int j = 1;
		while (i < image.getWidth() - 1) {
			j = 1;
			while (j < image.getHeight() - 1) {
				Color oldColor = pxReader.getColor(i, j);

				Color[] colors = null;

				if (metodo == RUIDO_CRUZ) {

					colors = new Color[4];
					colors[0] = pxReader.getColor(i, j - 1);
					colors[1] = pxReader.getColor(i, j + 1);
					colors[2] = pxReader.getColor(i - 1, j);
					colors[3] = pxReader.getColor(i + 1, j);
				} else if (metodo == RUIDO_3X3) {
					colors = new Color[8];
					colors[0] = pxReader.getColor(i, j - 1);
					colors[1] = pxReader.getColor(i, j + 1);
					colors[2] = pxReader.getColor(i - 1, j);
					colors[3] = pxReader.getColor(i + 1, j);
					colors[4] = pxReader.getColor(i - 1, j + 1);
					colors[5] = pxReader.getColor(i + 1, j - 1);
					colors[6] = pxReader.getColor(i - 1, j - 1);
					colors[7] = pxReader.getColor(i + 1, j + 1);
				} else {
					colors = new Color[4];
					colors[0] = pxReader.getColor(i - 1, j + 1);
					colors[1] = pxReader.getColor(i + 1, j - 1);
					colors[2] = pxReader.getColor(i - 1, j - 1);
					colors[3] = pxReader.getColor(i + 1, j + 1);
				}

				Color newColor = getColorMediaMediana(colors, selecao == RUIDO_MEDIANA);

				pxWriter.setColor(i, j,
						new Color(newColor.getRed(), newColor.getGreen(), newColor.getBlue(), oldColor.getOpacity()));
				j++;
			}
			i++;
		}

		return wImage;
	}

	/**
	 * Obt�m a m�dia ou mediana do array de cores recebido.
	 * 
	 * @param colors
	 *            Array de Cores.
	 * @param isMediana
	 *            indica se � mediana, caso contr�rio � m�dia.
	 * @return
	 */
	private static Color getColorMediaMediana(Color[] colors, boolean isMediana) {

		Color retorno = null;

		double red = 0;
		double blue = 0;
		double green = 0;
		double opacity = 0;

		if (!isMediana) {
			for (Color color : colors) {
				red += color.getRed();
				blue += color.getBlue();
				green += color.getGreen();
				opacity = color.getOpacity();
			}

			int div = colors.length;

			retorno = new Color(red / div, green / div, blue / div, opacity / div);
		} else {
			double[] arrRed = new double[colors.length];
			double[] arrBlue = new double[colors.length];
			double[] arrGreen = new double[colors.length];

			int i = 0;

			do {
				arrRed[i] = colors[i].getRed();
				arrBlue[i] = colors[i].getBlue();
				arrGreen[i] = colors[i].getGreen();
			} while (++i < colors.length);

			int indexCut = colors.length % 2 == 0 ? (colors.length / 2) - 1 : (colors.length - 1) / 2;

			QuickSort.observer = colors.clone();
			QuickSort.order(arrRed, 0, arrRed.length - 1);

			// Color colorRed = (Color) QuickSort.observer[indexCut];

			QuickSort.observer = colors.clone();
			QuickSort.order(arrBlue, 0, arrBlue.length - 1);

			// Color colorBlue = (Color) QuickSort.observer[indexCut];

			QuickSort.observer = colors.clone();
			QuickSort.order(arrGreen, 0, arrGreen.length - 1);

			// Color colorGreen = (Color) QuickSort.observer[indexCut];

			retorno = new Color(arrRed[indexCut], arrGreen[indexCut], arrBlue[indexCut], opacity);
			// retorno = new Color(0.69, 0.29, 0.63, opacity);

		}

		return retorno;
	}

	/**
	 * Obtem o histograma da imagem.
	 * @param image Imagem a ser trabalhada
	 * @param getsAcumulated caso true, retorna a partir da posi��o [3] o histograma aculumado.
	 * @return
	 */
	private static int[][] getHistograma(Image image, boolean getsAcumulated) {
		int[][] retorno = new int[getsAcumulated ? 6 : 3][256];

		int red = 0;
		int blue = 1;
		int green = 2;
		int redA = 3;
		int greenA = 4;
		int blueA = 5;
		

		PixelReader pxReader = image.getPixelReader();
		int i = 1;
		int j = 1;
		while (i < image.getWidth() - 1) {
			j = 1;
			while (j < image.getHeight() - 1) {
				Color oldColor = pxReader.getColor(i, j);

				retorno[red][(int) (oldColor.getRed() * 255)]++;
				retorno[blue][(int) (oldColor.getBlue() * 255)]++;
				retorno[green][(int) (oldColor.getGreen() * 255)]++;
				j++;
			}
			i++;
		}
		
		if(getsAcumulated) {
			i = 0;
			int count = -1;
			
			qtdTonsRed = 0;
			qtdTonsGreen = 0;
			qtdTonsBlue = 0;
			
			while (i < retorno[red].length) {
				retorno[redA][i] = (count == -1 ? 0 : retorno[redA][count]) + retorno[red][i];
				retorno[blueA][i] = (count == -1 ? 0 : retorno[blueA][count]) + retorno[blue][i];
				retorno[greenA][i] = (count == -1 ? 0 : retorno[greenA][count]) + retorno[green][i];
				
				if(retorno[redA][i] == 0) {
					qtdTonsRed++;
				}
				
				if(retorno[blueA][i] == 0) {
					qtdTonsGreen++;
				}
				
				if(retorno[greenA][i] == 0) {
					qtdTonsBlue++;
				}
				
				count++;
				i++;
			}
		}

		return retorno;
	}
	
	public static Image getEqualizacaoHistograma(Image image) {
		
		Image retorno = null;
		
		int[][] histogramaImage = getHistograma(image, true);
		
		double qtdPixelImage = image.getWidth()*image.getHeight();
		
		PixelReader pxReader = image.getPixelReader();
		WritableImage wImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
		PixelWriter pxWriter = wImage.getPixelWriter();
		int i = 1;
		int j = 1;
		while (i < image.getWidth()) {
			j = 1;
			while (j < image.getHeight()) {
				Color oldColor = pxReader.getColor(i, j);

				double histRed = histogramaImage[3][(int) (oldColor.getRed()*255)];
				double histGreen = histogramaImage[4][(int) (oldColor.getGreen()*255)];
				double histBlue = histogramaImage[5][(int) (oldColor.getBlue()*255)];
				
				double yRed = (((255 - qtdTonsRed) -1)/qtdPixelImage) * histRed;
				double yGreen = (((255 - qtdTonsGreen) -1)/qtdPixelImage) * histGreen;
				double yBlue = (((255 - qtdTonsBlue) -1)/qtdPixelImage) * histBlue;
				
				pxWriter.setColor(i, j, new Color(Math.abs(yRed)/255, Math.abs(yGreen)/255, Math.abs(yBlue)/255, oldColor.getOpacity()));
				j++;
			}
			i++;
		}
		
		retorno = wImage;
		
		return retorno;
		
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void getGrafico(Image img, BarChart<String, Number> grafico) {
		CategoryAxis eixoX = new CategoryAxis();
		NumberAxis eixoY = new NumberAxis();
		grafico.setTitle("Histograma");
		eixoX.setLabel("Canal");
		eixoY.setLabel("valor");
		
		int[][] histograma = getHistograma(img, false);
		
		XYChart.Series vlrR = new XYChart.Series();
		vlrR.setName("R");
		XYChart.Series vlrG = new XYChart.Series();
		vlrG.setName("G");
		XYChart.Series vlrB = new XYChart.Series();
		vlrB.setName("B");
		
		for (int i = 0; i < histograma[0].length; i++) {
			vlrR.getData().add(new XYChart.Data(i + "", histograma[0][i]));
			vlrG.getData().add(new XYChart.Data(i + "", histograma[1][i]));
			vlrB.getData().add(new XYChart.Data(i + "", histograma[2][i]));
		}

		grafico.getData().addAll(vlrR, vlrG, vlrB);
	}
	
	/**
	 * Rotaciona uma imagem
	 * @param img
	 * @return
	 */
	public static Image getImageRotate(Image img) {
		
		Image retorno = null;
		
		int height = (int)img.getHeight();
		
		PixelReader pxReader = img.getPixelReader();
		WritableImage wImage = new WritableImage(height, (int) img.getWidth());
		PixelWriter pxWriter = wImage.getPixelWriter();
		int i = 1;
		int j = 1;
		
		while (i < img.getWidth()) {
			j = 1;
			while (j < img.getHeight()) {
				Color oldColor = pxReader.getColor(i, j);				
				pxWriter.setColor(height-j-1, i, oldColor);
				j++;
			}
			i++;
		}
		
		retorno = wImage;
		
		return retorno;
	}

	/**
	 * Rotaciona uma imagem
	 * @param img
	 * @return
	 */
	public static Image getImageScaled(Image img, boolean more) {
		
		Image retorno = null;
		
		int width = (more) ? (int) img.getWidth()*2 : (int) img.getWidth()/2;
		int height = (more) ? (int) img.getHeight()*2 : (int) img.getHeight()/2;
		System.out.println("width " + width  +" height " + height);
		PixelReader pxReader = img.getPixelReader();
		WritableImage wImage = new WritableImage(width,height);
		PixelWriter pxWriter = wImage.getPixelWriter();
		int i = 1;
		int j = 1;
		int iNew = 1;
		int jNew = 1;
		
		if(more) {
			width = (int)img.getWidth();
			height = (int)img.getHeight();
		}
		
		while (i < width -1) {
			j = 1;
			jNew = 1;
			while (j < height -1) {
				
				try {
					if(more) {
						Color oldColor = pxReader.getColor(i, j);
						pxWriter.setColor(iNew, jNew, oldColor);
						pxWriter.setColor(iNew, jNew - 1, oldColor);
						pxWriter.setColor(iNew, jNew + 1, oldColor);
						pxWriter.setColor(iNew - 1, jNew, oldColor);
						pxWriter.setColor(iNew + 1, jNew, oldColor);
						pxWriter.setColor(iNew - 1, jNew + 1, oldColor);
						pxWriter.setColor(iNew + 1, jNew - 1, oldColor);
						pxWriter.setColor(iNew - 1, jNew - 1, oldColor);
						pxWriter.setColor(iNew + 1, jNew + 1, oldColor);
					} else {
						Color oldColor = pxReader.getColor(i * 2, j*2);
						pxWriter.setColor(i,j, oldColor);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
				j++;
				jNew += 2;
			}
			i++;
			iNew+=2;
		}
		
		retorno = wImage;
		
		return retorno;
	}
	
	public static Image getImagemAdicaoSubtracao(Image img1, double opacity1, Image img2, double opacity2, boolean adicao) {
		
		Image retorno = null;
		
		try {
		
			double width = Math.min(img1.getWidth(), img2.getWidth());
			double height = Math.min(img1.getHeight(), img2.getHeight());
			System.out.println("width " + width  +" height " + height);
			PixelReader pxReader1 = img1.getPixelReader();
			PixelReader pxReader2 = img2.getPixelReader();
			WritableImage wImage = new WritableImage((int)width,(int)height);
			PixelWriter pxWriter = wImage.getPixelWriter();
			int i = 1;
			int j = 1;
			while (i < wImage.getWidth() -1) {
				j = 1;
				while (j < wImage.getHeight() -1) {

						Color oldColor1 = pxReader1.getColor(i, j);
					
						Color oldColor2 = pxReader2.getColor(i, j);
	
						Color newColor = null;
						
						if(adicao) {
							double red = (oldColor1.getRed() * (opacity1/100)) + (oldColor2.getRed() * (opacity2/100));
							double green = (oldColor1.getGreen() * (opacity1/100)) + (oldColor2.getGreen() * (opacity2/100));
							double blue = (oldColor1.getBlue() * (opacity1/100)) + (oldColor2.getBlue() * (opacity2/100));
							newColor = new Color(red, green, blue, oldColor1.getOpacity());
						} else {
							double red = oldColor1.getRed() - oldColor2.getRed();
							double green = oldColor1.getGreen() - oldColor2.getGreen();
							double blue = oldColor1.getBlue() - oldColor2.getBlue();
							newColor = new Color(red < 0 ? 0 : red, green < 0 ? 0 : green, blue < 0 ? 0 : blue, oldColor1.getOpacity());
						}
						
						pxWriter.setColor(i, j, newColor);

					j++;
				}
				i++;
			}
			
			retorno = wImage;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return retorno;
	}
	
	
}
