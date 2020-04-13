package br.com.samuka.opencv.controller;

import java.awt.Event;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.osgi.OpenCVInterface;
//import org.opencv.videoio.VideoCapture;
import org.opencv.core.Size;
import br.com.samuka.opencv.main.Main;
import br.com.samuka.opencv.util.ImageManipule;
import br.com.samuka.opencv.util.Utils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController extends TrabalhoFinal implements Runnable, Serializable {

    //public static String absolutePath = "C:\\Users\\Samuel\\workspace\\PDIFINAL\\imgs\\";
    public static String absolutePath = System.getProperty("user.dir") + "\\imgs\\";

    public static String ABSOLUTE_PATH_WORK = "operation\\";

    private final FileChooser fileChooser = new FileChooser();

    final ContextMenu contextMenu;

    private ImageView lastImViewPressed;

    private String lastImageURL;

    @FXML
    private Slider slLimiarizacao;

    @FXML
    private TabPane tpParametros;

    @FXML
    private ImageView iv2;

    @FXML
    private AnchorPane imPane3;

    @FXML
    public static SplitPane spImages;

    @FXML
    private ImageView iv1;

    @FXML
    private AnchorPane imPane2;

    @FXML
    private AnchorPane imPane1;

    @FXML
    private ImageView iv3;

    @FXML
    private Text tRed;

    @FXML
    private Text tGren;

    @FXML
    public static Text tBlue;

    @FXML
    private TextField mediaB;

    @FXML
    private TextField mediaR;

    @FXML
    private TextField mediaG;

    @FXML
    private Button btMediaPonderada;

    @FXML
    private TextField tfQtdPontoCorte;

    @FXML
    private ScrollPane sp1;

    @FXML
    private ScrollPane sp2;

    @FXML
    private ScrollPane sp3;

    @FXML
    private RadioButton rd3x3;

    @FXML
    private RadioButton rdCruz;

    @FXML
    private RadioButton rdX;

    @FXML
    private RadioButton rdMedia;

    @FXML
    private RadioButton rdMediana;

    @FXML
    private Button btActionRuido;

    @FXML
    private Button btReexecutarRuido;

    @FXML
    private RadioButton rdImagem1Histograma;

    @FXML
    private RadioButton rdImagem2Histograma;

    @FXML
    private RadioButton rdImagem3Histograma;

    @FXML
    private Button btGerarHistograma;

    @FXML
    private Button btGerarImagemEqualizada;

    @FXML
    private Button btGirarImagem;

    @FXML
    private Button btAumentarImagem;

    @FXML
    private Button btDIminuirImagem;

    @FXML
    private TextField tfAdicaoSubtracaoT1;

    @FXML
    private TextField tfAdicaoSubtracaoT2;

    @FXML
    private Button btAdicao;

    @FXML
    private Button btSubtracao;

    @FXML
    private Button btBordasCanny;

    @FXML
    private Button btBordasRoberts;

    @FXML
    private Button btBordasSobel;

    @FXML
    private Button btErosao;

    @FXML
    private Button btDilatacao;

    @FXML
    private Button btSegmentacao;

    @FXML
    private TextField txDy, txDx, txDdepth;

    public MainController() {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        fileChooser.setTitle("Selecione uma imagem");
        fileChooser.setInitialDirectory(new File(absolutePath));

        MenuItem cut = new MenuItem("Carregar Imagem");
        MenuItem save = new MenuItem("Salvar Imagem");

        cut.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                File file = fileChooser.showOpenDialog(null);
                if (file != null) {
                    loadFile(file);
                }

            }
        });

        save.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                if (lastImViewPressed.getImage() != null) {

                    File file = fileChooser.showSaveDialog(null);
                    BufferedImage buffer = SwingFXUtils.fromFXImage(lastImViewPressed.getImage(), null);

                    try {
                        ImageIO.write(buffer, "png", file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }
        });

        contextMenu = new ContextMenu(cut, save);

    }

    @FXML
    void onClickIv(ActionEvent ev) {
    }

    @FXML
    void onActionBtActionRuido(ActionEvent ev) {
        System.err.println("processar ruído");
        processarRuido(iv1, iv2);
        btReexecutarRuido.setVisible(true);
    }

    @FXML
    void onActionBtReexecutarRuido(ActionEvent ev) {
        System.err.println("reprocessar ruído");
        processarRuido(iv2, iv2);
    }

    @FXML
    void onActionBtGirarImagem(ActionEvent ev) {
        System.err.println("girando imagem");

        Image img = iv1.getImage();

        if (iv2.getImage() != null) {
            img = iv2.getImage();
        }

        Image image = ImageManipule.getImageRotate(img);
        setImage(sp2, iv2, imPane2, image);

    }

    @FXML
    void onActionBtAumentarImagem(ActionEvent ev) {
        System.err.println("aumentar imagem");
        Image image = ImageManipule.getImageScaled(iv1.getImage(), true);
        setImage(sp2, iv2, imPane2, image);
    }

    @FXML
    void onActionBtDiminuirImagem(ActionEvent ev) {
        System.err.println("diminuir imagem");
        Image image = ImageManipule.getImageScaled(iv1.getImage(), false);
        setImage(sp2, iv2, imPane2, image);
    }

    @FXML
    void onActionBtAdicaoImagem(ActionEvent ev) {
        System.err.println("adição imagem");
        Image image = ImageManipule.getImagemAdicaoSubtracao(iv1.getImage(), Double.parseDouble(tfAdicaoSubtracaoT1.getText()), iv2.getImage(), Double.parseDouble(tfAdicaoSubtracaoT2.getText()), true);
        setImage(sp3, iv3, imPane3, image);
    }

    @FXML
    void onActionBtSubtracaoImagem(ActionEvent ev) {
        System.err.println("subtração imagem");
        Image image = ImageManipule.getImagemAdicaoSubtracao(iv1.getImage(), Double.parseDouble(tfAdicaoSubtracaoT1.getText()), iv2.getImage(), Double.parseDouble(tfAdicaoSubtracaoT2.getText()), false);
        setImage(sp3, iv3, imPane3, image);
    }

    @FXML
    void onActionBtErosao(ActionEvent ev) {
        System.err.println("Erosão");

        Mat source = Imgcodecs.imread(lastImageURL, Imgcodecs.CV_LOAD_IMAGE_COLOR);
        Mat destination = new Mat(source.rows(), source.cols(), source.type());

        int erosion_size = 5;

        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2 * erosion_size + 1, 2 * erosion_size + 1));
        Imgproc.erode(source, destination, element);
        Imgcodecs.imwrite(absolutePath + "erosao.jpg", destination);

        Image image = new Image("file:\\" + absolutePath + "erosao.jpg");

        setImage(sp2, iv2, imPane2, image);
    }

    @FXML
    void onActionBtDilatacao(ActionEvent ev) {
        System.err.println("Dilatação");

        Mat source = Imgcodecs.imread(lastImageURL, Imgcodecs.CV_LOAD_IMAGE_COLOR);
        Mat destination = new Mat(source.rows(), source.cols(), source.type());
        //2 * dilation_size
        Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(4, 4));
        Imgproc.dilate(source, destination, element1);
        Imgcodecs.imwrite(absolutePath + "dilatacao.jpg", destination);

        Image image = new Image("file:\\" + absolutePath + "dilatacao.jpg");

        setImage(sp2, iv2, imPane2, image);

    }

    @FXML
    void onActionBtSegmentacao(ActionEvent ev) {
        System.err.println("Segmentação");

        Mat source = Imgcodecs.imread(lastImageURL, Imgcodecs.CV_LOAD_IMAGE_COLOR);

        Mat destination = new Mat(source.rows(), source.cols(), source.type());

        Imgproc.threshold(source, destination, 127, 255, Imgproc.THRESH_BINARY_INV);

        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 1));

        Imgproc.morphologyEx(destination, destination, Imgproc.MORPH_CLOSE, kernel);

        Imgproc.cvtColor(destination, destination, Imgproc.COLOR_BGR2GRAY);

        Imgcodecs.imwrite(absolutePath + "segmentacaoBinary.jpg", destination);

        Image image = new Image("file:\\" + absolutePath + "segmentacaoBinary.jpg");

        setImage(sp2, iv2, imPane2, image);

    }

    @FXML
    void onActionBtBordaCanny(ActionEvent ev) {
        System.err.println("Detecção de bordas Canny");

        Image imageGray = ImageManipule.getImageGrayScale(iv1.getImage(), 0, 0, 0);

        BufferedImage buffer = SwingFXUtils.fromFXImage(imageGray, null);

        String fileName = absolutePath + "grayScaleOpenCV.jpg";

        File file = new File(fileName);

        try {
            ImageIO.write(buffer, "jpg", file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Mat source = Imgcodecs.imread(file.getAbsolutePath(), Imgcodecs.CV_LOAD_IMAGE_COLOR);
        Mat destination = new Mat(source.rows(), source.cols(), source.type());

        //100, 200, 7
        Imgproc.Canny(source, destination, Integer.parseInt(txDdepth.getText()), Integer.parseInt(txDx.getText()), Integer.parseInt(txDy.getText()), false);
        //certo - 300 - 130 - 3

        Imgcodecs.imwrite(absolutePath + "canny.jpg", destination);

        Image image = new Image("file:\\" + absolutePath + "canny.jpg");

        setImage(sp2, iv2, imPane2, image);
    }

    @FXML
    void onActionBtBordaSobel(ActionEvent ev) {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        System.err.println("Detecção de bordas sobel");

        Image imageGray = ImageManipule.getImageGrayScale(iv1.getImage(), 0, 0, 0);

        BufferedImage buffer = SwingFXUtils.fromFXImage(imageGray, null);

        String fileName = absolutePath + "grayScaleOpenCV.jpg";

        File file = new File(fileName);

        try {
            ImageIO.write(buffer, "jpg", file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Mat source = Imgcodecs.imread(lastImageURL, Imgcodecs.CV_LOAD_IMAGE_COLOR);
        Mat destination = new Mat(source.rows(), source.cols(), source.type());

        Imgproc.Sobel(source, destination, Integer.parseInt(txDdepth.getText()), Integer.parseInt(txDx.getText()), Integer.parseInt(txDy.getText()), 3, 1, 0);

        Imgcodecs.imwrite(absolutePath + "sobel.jpg", destination);

        Image image = new Image("file:\\" + absolutePath + "sobel.jpg");

        setImage(sp2, iv2, imPane2, image);

    }

    private void processarRuido(ImageView objetivo, ImageView destino) {

        int i = rdCruz.isSelected() ? ImageManipule.RUIDO_CRUZ : (rd3x3.isSelected() ? ImageManipule.RUIDO_3X3 : ImageManipule.RUIDO_X);
        int j = rdMedia.isSelected() ? ImageManipule.RUIDO_MEDIA : ImageManipule.RUIDO_MEDIANA;

        Image img = ImageManipule.getImagemSemRuido(objetivo.getImage(), i, j);
        setImage(sp2, destino, imPane2, img);

    }

    @FXML
    void onActionBtImagemEqualizada(ActionEvent ev) {
        if (rdImagem1Histograma.isSelected()) {
            Image image = ImageManipule.getEqualizacaoHistograma(iv1.getImage());
            setImage(sp2, iv2, imPane2, image);
        } else if (rdImagem2Histograma.isSelected()) {
            Image image = ImageManipule.getEqualizacaoHistograma(iv2.getImage());
            setImage(sp3, iv3, imPane3, image);
        } else {
            Image image = ImageManipule.getEqualizacaoHistograma(iv3.getImage());
            setImage(sp1, iv1, imPane1, image);
        }
    }

    @FXML
    void onImageViewClick(MouseEvent event) {

        ImageView imView;

        try {
            
            if (event.getTarget() instanceof ImageView) {
                imView = (ImageView) event.getTarget();

                lastImViewPressed = imView;

                if (event.getButton().equals(MouseButton.SECONDARY)) {
                    contextMenu.show(Main.root, event.getScreenX(), event.getScreenY());
                    new Thread(this).start();
                }
            }
        } catch (Exception r) {
            r.printStackTrace();
        }
    }

    @Override
    public void run() {

        try {
            Thread.sleep(3000);
            contextMenu.hide();
        } catch (Exception e) {
        }

    }

    private void loadFile(File file) {
        sp1.setMaxWidth(imPane1.getWidth());
        sp1.setMaxHeight(imPane1.getHeight());
        Image img = new Image("file:\\" + file.getAbsolutePath());
        lastImViewPressed.setFitHeight(imPane1.getHeight());
        lastImViewPressed.setFitHeight(img.getHeight());
        lastImViewPressed.setFitWidth(img.getWidth());
        lastImViewPressed.setImage(img);
        lastImageURL = file.getAbsolutePath();

    }

    @FXML
    /**
     * Detecta o movimento do mouse sobre a imagem e obtem os pixels da mesma.
     *
     * @param event
     */
    void onImageViewMouseMove(MouseEvent event) {

        ImageView imageView = (ImageView) event.getTarget();

        double x = event.getX();
        double y = event.getY();

        if (imageView.getImage() != null) {

            try {
                Color color = (imageView.getImage().getPixelReader() != null) ? imageView.getImage().getPixelReader().getColor((int) x, (int) y) : null;

                if (color != null) {

                    DecimalFormat format = new DecimalFormat("#");
                    try {
                        tRed.setText("" + format.format(color.getRed() * 255));
                        tBlue.setText("" + format.format((int) color.getBlue() * 255));
                        tGren.setText("" + format.format((int) color.getGreen() * 255));
                    } catch (Exception e) {

                    }
                }
            } catch (Exception e) {

            }

        }

    }

    public void initialize() {
        slLimiarizacao.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {

                int value = -1;
                try {
                    value = Integer.parseInt(tfQtdPontoCorte.getText());
                } catch (Exception e) {
                }
                onSliderEvent(value);
            }
        });

        tfQtdPontoCorte.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (!newPropertyValue) {
                    try {
                        onSliderEvent(Integer.parseInt(tfQtdPontoCorte.getText()));
                    } catch (Exception e) {

                    }
                }
            }
        });

        ToggleGroup groupMetodo = new ToggleGroup();

        ToggleGroup groupMediana = new ToggleGroup();

        ToggleGroup groupHistograma = new ToggleGroup();

        rd3x3.setToggleGroup(groupMetodo);
        rdX.setToggleGroup(groupMetodo);
        rdCruz.setToggleGroup(groupMetodo);
        rd3x3.setSelected(true);

        rdMedia.setToggleGroup(groupMediana);
        rdMediana.setToggleGroup(groupMediana);
        rdMedia.setSelected(true);

        rdImagem1Histograma.setToggleGroup(groupHistograma);
        rdImagem2Histograma.setToggleGroup(groupHistograma);
        rdImagem3Histograma.setToggleGroup(groupHistograma);
        rdImagem1Histograma.setSelected(true);

        tfAdicaoSubtracaoT1.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (!newPropertyValue) {
                    try {
                        int opacity = Integer.parseInt(tfAdicaoSubtracaoT1.getText());
                        tfAdicaoSubtracaoT2.setText("" + (100 - opacity));
                    } catch (Exception e) {

                    }
                }
            }
        });

        tfAdicaoSubtracaoT2.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (!newPropertyValue) {
                    try {
                        int opacity = Integer.parseInt(tfAdicaoSubtracaoT2.getText());
                        tfAdicaoSubtracaoT1.setText("" + (100 - opacity));
                    } catch (Exception e) {

                    }
                }
            }
        });

    }

    @FXML
    private void btGrayScale(ActionEvent event) {

        if (((Button) event.getTarget()) == btMediaPonderada) {
            iv2.setImage(ImageManipule.getImageGrayScale(iv1.getImage(), Integer.parseInt(mediaR.getText()), Integer.parseInt(mediaG.getText()), Integer.parseInt(mediaB.getText())));
        } else {
            iv2.setImage(ImageManipule.getImageGrayScale(iv1.getImage(), 0, 0, 0));
        }

    }

    @FXML
    private void btNegativa(ActionEvent event) {
        iv2.setImage(ImageManipule.getImageNegativa(iv1.getImage()));
    }

    @FXML
    void onActionBtGerarHistograma(ActionEvent ev) {

        System.err.println("gerar histograma");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/graficoView.fxml"));

            Parent root = loader.load();

            Stage stage = new Stage();

            stage.setScene(new Scene(root));
            stage.setTitle("Histograma");
            stage.initOwner(((Node) ev.getSource()).getScene().getWindow());
            stage.show();

            GraficoController graficoController = (GraficoController) loader.getController();

            if (rdImagem1Histograma.isSelected()) {
                ImageManipule.getGrafico(iv1.getImage(), graficoController.grafico);
            } else if (rdImagem2Histograma.isSelected()) {
                ImageManipule.getGrafico(iv2.getImage(), graficoController.grafico);
            } else {
                ImageManipule.getGrafico(iv3.getImage(), graficoController.grafico);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void onSliderEvent(int zebrado) {
        double limiarIndex = slLimiarizacao.getValue() / 255;
        if (iv1.getImage() != null) {
            Image img = ImageManipule.getImageLimiarizacao(iv1.getImage(), limiarIndex, zebrado, true);
            sp2.setMaxWidth(imPane2.getWidth());
            sp2.setMaxHeight(imPane2.getHeight());
            iv2.setFitHeight(imPane2.getHeight());
            iv2.setFitHeight(img.getHeight());
            iv2.setFitWidth(img.getWidth());
            iv2.setImage(img);
        }
    }

    private void setImage(ScrollPane scrollPane, ImageView imView, AnchorPane anchorPane, Image img) {
        scrollPane.setMaxWidth(anchorPane.getWidth());
        scrollPane.setMaxHeight(anchorPane.getHeight());
        imView.setFitHeight(anchorPane.getHeight());
        imView.setFitHeight(img.getHeight());
        imView.setFitWidth(img.getWidth());
        imView.setImage(img);
    }

    private void showAlert(AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    void onBtAjustarInclinacaoImagem(ActionEvent ev) {
        System.err.println("ajustar inclinação Imagem");

        String path = MainController.absolutePath + MainController.ABSOLUTE_PATH_WORK;

        TrabalhoFinal trabalhoFinal = new TrabalhoFinal();

        //primeiro é necessário aplicar canny para obter imagem
        trabalhoFinal.getCanny(iv1.getImage(), path, 330, 100, 3, null);
        trabalhoFinal.getDilatation(path + "canny.jpg", path, null);

        Mat source = Imgcodecs.imread(path + "dilatacao.jpg", 0);

        Mat destination = new Mat(source.rows(), source.cols(), source.type());

        Imgproc.cvtColor(source, destination, Imgproc.COLOR_BayerBG2BGR);

        Mat lines = new Mat();

        ArrayList<Point[]> pontos = new ArrayList<>();

        int xyMin = 10;

        if (true) {

            Imgproc.HoughLines(source, lines, 1, Imgproc.CV_HOUGH_PROBABILISTIC, 200);

            for (int i = 0; i < lines.elemSize(); i++) {

                double[] retorno = lines.get(i, 0);
                if (retorno != null) {
                    double rho = retorno[0];
                    double theta = retorno[1];

                    Point pt1 = new Point(), pt2 = new Point();

                    double a = Math.cos(theta);
                    double b = Math.sin(theta);
                    double x0 = a * rho;
                    double y0 = b * rho;
                    pt1.x = x0 + 1000 * (-b);
                    pt1.y = y0 + 1000 * (a);
                    pt2.x = x0 - 1000 * (-b);
                    pt2.y = y0 - 1000 * (a);

                    if (pt1.x > xyMin && pt2.x > xyMin) {
                        pontos.add(new Point[]{pt1, pt2});
                        Imgproc.line(destination, pt1, pt2, new Scalar(0, 0, 255));
                    }
                }
            }
        } else {

            Imgproc.HoughLinesP(source, lines, 1, Imgproc.CV_HOUGH_PROBABILISTIC, 50, 10, 10);
            
            for (int i = 0; i < lines.elemSize(); i++) {

                double[] retorno = lines.get(i, 0);
                if (retorno != null) {
                    double v1 = retorno[0];
                    double v2 = retorno[1];
                    double v3 = retorno[2];
                    double v4 = retorno[3];

                    Point pt1 = new Point(v1, v2), pt2 = new Point(v3, v4);

                    Imgproc.line(destination, pt1, pt2, new Scalar(0, 0, 255));
                }
            }
        }

        Imgcodecs.imwrite(path + "houghLine.jpg", destination);

        Image image = new Image("file:\\" + path + "houghLine.jpg");

        setImage(sp2, iv2, imPane2, image);

        Image imageDest = new Image("file:\\" + lastImageURL);

        Image img = trabalhoFinal.getSudokuImage(path + "dilatacao.jpg", pontos, imageDest);

        if (img == null) {
            ajustRotateImage(lastImageURL, -2);
            onBtAjustarInclinacaoImagem(ev);
        }

        setImage(sp3, iv3, imPane3, img);
    }

    @FXML
    void onBtTestarLinhas(ActionEvent ev) {

        String path = MainController.absolutePath + "testarLinha\\";

        TrabalhoFinal trabalhoFinal = new TrabalhoFinal();

        //primeiro é necessário aplicar canny para obter imagem
        trabalhoFinal.getCanny(iv1.getImage(), path, 30, 1, 3, null);
        trabalhoFinal.getDilatation(path + "canny.jpg", path, null);//path + "canny.jpg"

        Mat source = Imgcodecs.imread(path + "canny.jpg", 0);

        Mat destination = new Mat(source.rows(), source.cols(), source.type());

        Imgproc.cvtColor(source, destination, Imgproc.COLOR_BayerBG2BGR);

        Mat lines = new Mat();//source.rows(),source.cols(),source.type()

        double ajustPer = 0;

        ArrayList<Point[]> pontos = new ArrayList<>();

        int xyMin = 10;

        if (true) {

            Imgproc.HoughLines(source, lines, 4, Imgproc.CV_HOUGH_PROBABILISTIC, 200);

            for (int i = 0; i < lines.elemSize(); i++) {

                double[] retorno = lines.get(i, 0);
                if (retorno != null) {
                    double rho = retorno[0];
                    double theta = retorno[1];

                    Point pt1 = new Point(), pt2 = new Point();

                    double a = Math.cos(theta);
                    double b = Math.sin(theta);
                    double x0 = a * rho;
                    double y0 = b * rho;
                    pt1.x = x0 + 1000 * (-b);
                    pt1.y = y0 + 1000 * (a);
                    pt2.x = x0 - 1000 * (-b);
                    pt2.y = y0 - 1000 * (a);

                    if (pt1.x > xyMin && pt2.x > xyMin) {
                        pontos.add(new Point[]{pt1, pt2});
                        Imgproc.line(destination, pt1, pt2, new Scalar(0, 0, 255));
                    }
                }
            }
        } else {

            //40, 50, 10
            Imgproc.HoughLinesP(source, lines, 1, Imgproc.CV_HOUGH_PROBABILISTIC, 50, 10, 10);

            double value1 = 0;
            double value2 = 0;
            for (int i = 0; i < lines.elemSize(); i++) {

                double[] retorno = lines.get(i, 0);
                if (retorno != null) {
                    double v1 = retorno[0];
                    double v2 = retorno[1];
                    double v3 = retorno[2];
                    double v4 = retorno[3];

                    if (i == 0) {
                        value1 = v4;
                    } else if (i == 1) {
                        value2 = v4;
                    }

                    Point pt1 = new Point(v1, v2), pt2 = new Point(v3, v4);

                    Imgproc.line(destination, pt1, pt2, new Scalar(0, 0, 255));
                }
                //break;
            }

            /*ajustPer = Math.abs(value1 - value2);

            Mat toRotate = Imgcodecs.imread("C:\\Users\\Samuel\\workspace\\PDIFINAL\\imgs\\2.jpg", 0);

            Point point = new Point(toRotate.cols() / 2, toRotate.rows() / 2);

            Mat toDestination = Imgproc.getRotationMatrix2D(point, ajustPer, 1.0);

            Rect bbox = new RotatedRect(point, toRotate.size(), ajustPer).boundingRect();

            Mat rotacionedImage = toRotate.clone();
            Imgproc.warpAffine(toRotate, rotacionedImage, toDestination, bbox.size());

            Imgcodecs.imwrite(absolutePath + "inclinacao.jpg", rotacionedImage);

            Image image = new Image("file:\\" + absolutePath + "inclinacao.jpg");
            setImage(sp2, iv2, imPane2, image);*/
        }

        Imgcodecs.imwrite(path + "houghLine.jpg", destination);

        Image image = new Image("file:\\" + path + "houghLine.jpg");

        setImage(sp2, iv2, imPane2, image);

        Image imageDest = new Image("file:\\" + lastImageURL);

        Image img = trabalhoFinal.getFinalGame(path + "dilatacao.jpg", pontos, imageDest);

        setImage(sp3, iv3, imPane3, img);

    }

    private void ajustRotateImage(String fileName, int rotate) {
        Mat toRotate = Imgcodecs.imread(fileName, 0);

        Point point = new Point(toRotate.cols() / 2, toRotate.rows() / 2);

        Mat toDestination = Imgproc.getRotationMatrix2D(point, rotate, 1.0);

        Rect bbox = new RotatedRect(point, toRotate.size(), rotate).boundingRect();

        Mat rotacionedImage = toRotate.clone();
        Imgproc.warpAffine(toRotate, rotacionedImage, toDestination, bbox.size());

        Imgcodecs.imwrite(absolutePath + "inclinacao.jpg", rotacionedImage);

        Image image = new Image("file:\\" + absolutePath + "inclinacao.jpg");
        lastImageURL = absolutePath + "inclinacao.jpg";
        setImage(sp1, iv1, imPane1, image);
    }

}
