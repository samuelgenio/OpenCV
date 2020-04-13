/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.samuka.opencv.util;

/**
 * Classe para ordena��o.
 * Podesse utilizar o objeto observer para ordenar outro array espelhado no array a ser ordenado.
 * Utilizar da seguinte maneira:
 * <pre>
 * QuickSort.observer = vetorEspelho;
 * QuickSort.order(vetor, 0, vetor.lenght);
 * </pre>
 *
 */
public class QuickSort {

    public static Object[] observer;
    
    public static void order(double[] vetor, int inicio, int fim) {
        if (inicio < fim) {
            int posicaoPivo = separar(vetor, inicio, fim);
            order(vetor, inicio, posicaoPivo - 1);
            order(vetor, posicaoPivo + 1, fim);
        }
    }

    private static int separar(double[] vetor, int inicio, int fim) {
        double pivo = vetor[inicio];
        
        Object pivoObserver = null;
        
        if(observer != null)
        	pivoObserver = observer[inicio];
        
        int i = inicio + 1, f = fim;
        while (i <= f) {
            if (vetor[i] <= pivo) {
                i++;
            } else if (pivo < vetor[f]) {
                f--;
            } else {
            	double troca = vetor[i];
                vetor[i] = vetor[f];
                vetor[f] = troca;
                
                if(observer != null) {
	                Object trocaObserver = observer[i];
	                observer[i] = observer[f];
	                observer[f] = trocaObserver;
                }
                
                i++;
                f--;
            }
        }
        vetor[inicio] = vetor[f];
        vetor[f] = pivo;
        
        if(observer != null) {
	        observer[inicio] = observer[f];
	        observer[f] = pivoObserver;
        }
        
        return f;
    }

}
