package xadrez;

import tabuleirojogo.Posicao;

public class PosicaoDoXadrez {

	private char coluna;
	private int linha;
	public PosicaoDoXadrez(char coluna, int linha) {
		if(coluna < 'a' || coluna > 'h' || linha < 1 || linha > 8) {
			throw new XadrezException("Erro instancioando a posição do xadrez. As posições válidas são de a1 até h8");			
		}
		this.linha = linha;
		this.coluna = coluna;
	}
	
	public int getLinha() {
		return linha;
	}
	
	public char getColuna() {
		return coluna;
	}
	
	protected Posicao posicionar() {
		return new Posicao(8-linha, coluna-'a');
	}
	
	protected static PosicaoDoXadrez daPosicao(Posicao posicao) {
		return new PosicaoDoXadrez((char)('a' + posicao.getColuna()), (char) (8 - posicao.getLinha()));
	}
	
	@Override
	public String toString() {
		return "" + coluna + linha;
	}
}
