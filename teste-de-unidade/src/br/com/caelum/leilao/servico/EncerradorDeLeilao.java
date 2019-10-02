package br.com.caelum.leilao.servico;

import java.util.Calendar;
import java.util.List;

import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.infra.dao.RepositorioDeLeiloes;
import br.com.caelum.leilao.infra.email.Carteiro;

public class EncerradorDeLeilao {

	private int encerrados;
	private final RepositorioDeLeiloes dao;
	private final Carteiro carteiro;

	public EncerradorDeLeilao(RepositorioDeLeiloes dao, Carteiro carteiro) {
		this.dao = dao;
		// guardamos o carteiro como atributo da classe
		this.carteiro = carteiro;
	}

	public void encerra() {
		List<Leilao> todosLeiloesCorrentes = dao.correntes();

		for (Leilao leilao : todosLeiloesCorrentes) {
			try {
				if (comecouSemanaPassada(leilao)) {
					encerrados++;
					leilao.encerra();
					dao.salva(leilao);
					carteiro.envia(leilao);
				}
			} catch (Exception e) {
				// salvo a excecao no sistema de logs
				// e o loop continua!
			}
		}
	}

	private boolean comecouSemanaPassada(Leilao leilao) {
		return diasEntre(leilao.getData(), Calendar.getInstance()) >= 7;
	}

	private int diasEntre(Calendar inicio, Calendar fim) {
		Calendar data = (Calendar) inicio.clone();
		int diasNoIntervalo = 0;
		while (data.before(fim)) {
			data.add(Calendar.DAY_OF_MONTH, 1);
			diasNoIntervalo++;
		}

		return diasNoIntervalo;
	}

	public int getQuantidadeDeEncerrados() {
		return encerrados;
	}
}
