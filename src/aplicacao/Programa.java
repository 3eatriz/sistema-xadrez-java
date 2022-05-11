package aplicacao;

import java.util.InputMismatchException;
import java.util.Scanner;

import xadrez.PartidaDeXadrez;
import xadrez.PecaDeXadrez;
import xadrez.PosicaoDoXadrez;
import xadrez.XadrezException;

public class Programa {

	public static void main(String[] args) {
		Scanner leia = new Scanner(System.in);
		PartidaDeXadrez partidaDeXadrez = new PartidaDeXadrez();
		
		while(true) {
			try {
				UI.clearScreen();
				UI.imprimirVez(partidaDeXadrez);
				System.out.println();
				System.out.print("Origem: ");
				PosicaoDoXadrez origem = UI.leiaPosicaoXadrez(leia);
				
				boolean[][] possiveisMovimentos = partidaDeXadrez.possiveisMovimentos(origem);
				UI.clearScreen();
				UI.imprimirTabuleiro(partidaDeXadrez.getPecas(), possiveisMovimentos);
				
				System.out.println();
				System.out.print("Destino: ");
				PosicaoDoXadrez destino = UI.leiaPosicaoXadrez(leia);
				
				PecaDeXadrez pecaCapturada = partidaDeXadrez.executarMovimento(origem, destino);
			}
			catch(XadrezException e){
				System.out.println(e.getMessage());
				leia.nextLine();
			}
			catch(InputMismatchException e){
				System.out.println(e.getMessage());
				leia.nextLine();
			}
		}
	}

}
