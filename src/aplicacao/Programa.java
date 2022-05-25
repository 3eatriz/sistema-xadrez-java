package aplicacao;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import xadrez.PartidaDeXadrez;
import xadrez.PecaDeXadrez;
import xadrez.PosicaoDoXadrez;
import xadrez.XadrezException;

public class Programa {

	public static void main(String[] args) {
		Scanner leia = new Scanner(System.in);
		PartidaDeXadrez partidaDeXadrez = new PartidaDeXadrez();
		List<PecaDeXadrez> capturadas = new ArrayList<>();
		
		while(!partidaDeXadrez.getXequeMate()) {
			try {
				UI.clearScreen();
				UI.imprimirPartida(partidaDeXadrez, capturadas);
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
				
				if(pecaCapturada != null) {
					capturadas.add(pecaCapturada);
				}
				
				if(partidaDeXadrez.getPromovido() != null) {
					System.out.print("Insira a pe�a para promo��o (B/N/R/Q): ");
					String tipo = leia.nextLine().toUpperCase();
					while(!tipo.equals("B") && !tipo.equals("C") && !tipo.equals("T") && !tipo.equals("Q")) {
						System.out.print("Valor inv�lido! Insira a pe�a para promo��o (B/N/R/Q): ");
						tipo = leia.nextLine().toUpperCase();
					}
					partidaDeXadrez.substituirPecaPromovida(tipo);
				}
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
		UI.clearScreen();
		UI.imprimirPartida(partidaDeXadrez, capturadas);
	}

}
