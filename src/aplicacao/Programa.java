package aplicacao;

import java.util.Scanner;

import xadrez.PartidaDeXadrez;
import xadrez.PecaDeXadrez;
import xadrez.PosicaoDoXadrez;

public class Programa {

	public static void main(String[] args) {
		Scanner leia = new Scanner(System.in);
		PartidaDeXadrez partidaDeXadrez = new PartidaDeXadrez();
		
		while(true) {
			UI.imprimirTabuleiro(partidaDeXadrez.getPecas());
			System.out.println();
			System.out.print("Origem: ");
			PosicaoDoXadrez origem = UI.leiaPosicaoXadrez(leia);
			
			System.out.println();
			System.out.print("Destino: ");
			PosicaoDoXadrez destino = UI.leiaPosicaoXadrez(leia);
			
			PecaDeXadrez pecaCapturada = partidaDeXadrez.executarMovimento(origem, destino);
		}
	}

}
