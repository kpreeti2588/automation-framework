package org.iomedia.galen.steps;

import org.iomedia.common.BaseUtil;
import org.iomedia.galen.common.Utils;
import org.iomedia.galen.pages.Invoice;

import cucumber.api.java.en.Then;

public class InvoiceSteps {
	
	Invoice invoice;
	Utils utils;
	BaseUtil base;
	org.iomedia.framework.Assert Assert;
	
	public InvoiceSteps(Invoice invoice, Utils utils, BaseUtil base, org.iomedia.framework.Assert Assert) {
		this.invoice = invoice;
		this.utils = utils;
		this.base = base;
		this.Assert = Assert;
	}
	
	@Then("^Pending invoice found on NAM with id (.+) and amount due (.+)$")
	public void pending_invoice_found_on_nam_with_id_and_amount_due(String invoiceId, String amtDue) {
		invoiceId = (String) base.getGDValue(invoiceId);
		amtDue = (String) base.getGDValue(amtDue);
		
		if(invoiceId.trim().contains("/"))
			invoiceId = invoiceId.trim().substring(0, invoiceId.trim().lastIndexOf("/"));
		invoice.isInvoiceSelected(Integer.valueOf(invoiceId), null);
		base.sync(5000L);
		Assert.assertTrue(invoice.isInvoiceDetailDisplayed(null), "Verify invoice detail block is displayed");
		invoice.getInvoice(amtDue);
		String invDue = invoice.amountDue();
		Assert.assertEquals(invDue, amtDue, "Verify invoice balance should match with the stp");
	}
}
