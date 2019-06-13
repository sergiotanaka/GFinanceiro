package org.pinguin.gf.service.api.planning;

import static java.math.BigDecimal.valueOf;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.Month;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pinguin.gf.domain.account.Account;
import org.pinguin.gf.domain.account.AccountNature;
import org.pinguin.gf.domain.account.AccountRepository;
import org.pinguin.gf.domain.planning.AccountPlanning;
import org.pinguin.gf.domain.planning.MonthYear;
import org.pinguin.gf.domain.planning.Planning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase
public class PdfReportGeneratorTest {

	@Autowired
	private AccountRepository accRepo;

	@Autowired
	private PdfReportGenerator gen;

	@Test
	public void testReport() throws IOException {

		final Account despesas = accRepo.save(newAccount("Despesas"));
		final Account compras = accRepo.save(newAccount("Compras", despesas));
		final Account saude = accRepo.save(newAccount("Saúde", despesas));
		final Account lazer = accRepo.save(newAccount("Lazer", despesas));
		final Account telefone = accRepo.save(newAccount("Telefone / Internet", despesas));

		final Planning planning = new Planning();
		final MonthYear monthYear = new MonthYear();
		monthYear.setMonth(Month.MAY);
		monthYear.setYear(2019);
		planning.setMonthYear(monthYear);
		planning.getAccountPlannings().add(newAccPlan(compras, valueOf(500.0)));
		planning.getAccountPlannings().add(newAccPlan(saude, valueOf(600.0)));
		planning.getAccountPlannings().add(newAccPlan(lazer, valueOf(300.0)));
		planning.getAccountPlannings().add(newAccPlan(telefone, valueOf(300.0)));
		final ByteArrayInputStream is = gen.build(planning);

		String userHome = System.getProperty("user.home");
		File targetFile = new File(userHome + "/test.pdf");
		@SuppressWarnings("resource")
		OutputStream outStream = new FileOutputStream(targetFile);
		byte[] buffer = new byte[8 * 1024];
		int bytesRead;
		while ((bytesRead = is.read(buffer)) != -1) {
			outStream.write(buffer, 0, bytesRead);
		}

	}

	private Account newAccount(final String name) {
		return new Account(name, AccountNature.DEBIT);
	}

	private Account newAccount(final String name, final Account parent) {
		return new Account(name, AccountNature.DEBIT, parent);
	}

	private AccountPlanning newAccPlan(final Account acc, BigDecimal value) {
		final AccountPlanning accPlan = new AccountPlanning();
		accPlan.setAccount(acc);
		accPlan.setValue(value);
		return accPlan;
	}

}
