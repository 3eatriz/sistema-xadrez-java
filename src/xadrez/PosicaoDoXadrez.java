package xadrez;

import tabuleirojogo.Posicao;

public class PosicaoDoXadrez {

	private char coluna;
	private int linha;
	public PosicaoDoXadrez(char coluna, int linha) {
		if(coluna < 'a' || coluna > 'h' || linha < 1 || linha > 8) {
			throw new XadrezException("Erro instancioando a posi��o do xadrez. As posi��es v�lidas s�o de a1 at� h8");			
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
