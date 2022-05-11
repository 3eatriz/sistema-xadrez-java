package xadrez;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import tabuleirojogo.Peca;
import tabuleirojogo.Posicao;
import tabuleirojogo.Tabuleiro;
import xadrez.pecas.Rei;
import xadrez.pecas.Torre;

public class PartidaDeXadrez {

	private int turno;
	private Cor jogadorAtual;
	private Tabuleiro tabuleiro;
	private boolean xeque;
	private boolean xequeMate;
	
	private List<Peca> pecasNoTabuleiro = new ArrayList<>();
	private List<Peca> pecasCapturadas = new ArrayList<>();
	
	public PartidaDeXadrez() {
		tabuleiro = new Tabuleiro(8, 8);
		turno = 1;
		jogadorAtual = Cor.BRANCO;
		ConfigInicial();
	}
	
	public int getTurno() {
		return turno;
	}
	
	public Cor getJogadorAtual() {
		return jogadorAtual;
	}
	
	public boolean getXeque() {
		return xeque;
	}
	
	public boolean getXequeMate(){
		return xequeMate;
	}
	
	public PecaDeXadrez[][] getPecas(){
		PecaDeXadrez[][] mat = new PecaDeXadrez[tabuleiro.getLinhas()][tabuleiro.getColunas()];
		for(int i=0; i<tabuleiro.getLinhas(); i++) {
			for(int j=0; j<tabuleiro.getColunas(); j++) {
				mat[i][j] = (PecaDeXadrez) tabuleiro.peca(i,j);
			}
		}
		return mat;
	}
	
	public boolean[][] possiveisMovimentos(PosicaoDoXadrez posicaoOrigem){
		Posicao posicao = posicaoOrigem.posicionar();
		validarPosicaoDeOrigem(posicao);
		return tabuleiro.peca(posicao).possiveisMovimentos();
	}
	
	public PecaDeXadrez executarMovimento(PosicaoDoXadrez posicaoDeOrigem, PosicaoDoXadrez posicaoDeDestino) {
		Posicao origem = posicaoDeOrigem.posicionar();
		Posicao destino = posicaoDeDestino.posicionar();
		validarPosicaoDeOrigem(origem);
		validarPosicaoDeDestino(origem, destino);
		Peca pecaCapturada = mover(origem, destino);
		
		if(testarXeque(jogadorAtual)) {
			desfazerMovimento(origem, destino, pecaCapturada);
			throw new XadrezException("Voc� n�o pode se colocar em xeque");
		}
		
		xeque = (testarXeque(inimigo(jogadorAtual))) ? true : false;
		
		if(testarXequeMate(inimigo(jogadorAtual))) {
			xequeMate = true;
		}
		else {
			proximoTurno();
		}
		
		return (PecaDeXadrez) pecaCapturada;
	}
	
	private Peca mover(Posicao origem, Posicao destino) {
		PecaDeXadrez p = (PecaDeXadrez)tabuleiro.removerPeca(origem);
		p.aumentarContagemMovimentos();
		Peca pecaCapturada = tabuleiro.removerPeca(destino);
		tabuleiro.colocarPeca(p, destino);
		
		if(pecaCapturada != null) {
			pecasNoTabuleiro.remove(pecaCapturada);
			pecasCapturadas.add(pecaCapturada);
		}
		return pecaCapturada;
	}
	
	private void desfazerMovimento(Posicao origem, Posicao destino, Peca pecaCapturada) {
		PecaDeXadrez p = (PecaDeXadrez)tabuleiro.removerPeca(destino);
		p.diminuirContagemMovimentos();
		tabuleiro.colocarPeca(p, origem);
		
		if(pecaCapturada != null) {
			tabuleiro.colocarPeca(pecaCapturada, destino);
			pecasCapturadas.remove(pecaCapturada);
			pecasNoTabuleiro.add(pecaCapturada);
		}
	}
	
	private void validarPosicaoDeOrigem(Posicao posicao) {
		if(!tabuleiro.haUmaPeca(posicao)) {
			throw new XadrezException("N�o existe uma pe�a na posi��o de origem");
		}
		if(jogadorAtual != ((PecaDeXadrez)tabuleiro.peca(posicao)).getCor()) {
			throw new XadrezException("A pe�a escolhida n�o � sua");
		}
		if(!tabuleiro.peca(posicao).existeMovimentoPossivel()) {
			throw new XadrezException("N�o existe movimentos poss�veis para a pe�a escolhida");
		}
	}
	
	private void validarPosicaoDeDestino(Posicao origem, Posicao destino) {
		if(!tabuleiro.peca(origem).podeMover(destino)) {
			throw new XadrezException("A pe�a escolhida n�o pode se mover para a posi��o do destino");
		}
	}
	
	private void proximoTurno() {
		turno++;
		jogadorAtual = (jogadorAtual == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
	}
	
	private Cor inimigo(Cor cor) {
		return (cor == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
	}
	
	private PecaDeXadrez rei(Cor cor) {
		List<Peca> lista = pecasNoTabuleiro.stream().filter(x -> ((PecaDeXadrez)x).getCor() == cor).collect(Collectors.toList());
		for(Peca p : lista) {
			if(p instanceof Rei) {
				return (PecaDeXadrez)p;
			}
		}
		throw new IllegalStateException("N�o existe o rei " + cor + " no tabuleiro");
	}
	
	private boolean testarXeque(Cor cor) {
		Posicao posicaoRei = rei(cor).getPosicaoDoXadrez().posicionar();
		List<Peca> pecasInimigas = pecasNoTabuleiro.stream().filter(x -> ((PecaDeXadrez)x).getCor() == inimigo(cor)).collect(Collectors.toList());
		for(Peca p : pecasInimigas) {
			boolean[][] mat = p.possiveisMovimentos();
			if(mat[posicaoRei.getLinha()][posicaoRei.getColuna()]) {
				return true;
			}
		}
		return false;
	}
	
	private boolean testarXequeMate(Cor cor){
		if(!testarXeque(cor)) {
			return false;
		}
		List<Peca> lista = pecasNoTabuleiro.stream().filter(x -> ((PecaDeXadrez)x).getCor() == cor).collect(Collectors.toList());
		for(Peca p : lista) {
			boolean[][] mat = p.possiveisMovimentos();
			for(int i=0; i<tabuleiro.getLinhas(); i++) {
				for(int j=0; j<tabuleiro.getColunas(); j++) {
					if(mat[i][j]) {
						Posicao origem = ((PecaDeXadrez)p).getPosicaoDoXadrez().posicionar();
						Posicao destino = new Posicao(i, j);
						Peca pecaCapturada = mover(origem, destino);
						boolean testarXeque = testarXeque(cor);
						desfazerMovimento(origem, destino, pecaCapturada);
						if(!testarXeque) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	private void colocarNovaPeca(char coluna, int linha, PecaDeXadrez peca) {
		tabuleiro.colocarPeca(peca, new PosicaoDoXadrez(coluna, linha).posicionar());
		pecasNoTabuleiro.add(peca);
	}
	
	private void ConfigInicial() {
		colocarNovaPeca('h', 7, new Torre(tabuleiro, Cor.BRANCO));
		colocarNovaPeca('d', 1, new Torre(tabuleiro, Cor.BRANCO));
		colocarNovaPeca('e', 1, new Rei(tabuleiro, Cor.BRANCO));
		
		colocarNovaPeca('b', 8, new Torre(tabuleiro, Cor.PRETO));
		colocarNovaPeca('a', 8, new Rei(tabuleiro, Cor.PRETO));
	}
	
}
