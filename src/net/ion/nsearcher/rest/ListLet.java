package net.ion.nsearcher.rest;

import java.util.List;

import net.ion.framework.rest.RopeRepresentation;
import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.rest.formater.SearchDocumentFormater;
import net.ion.nsearcher.search.SearchRequest;
import net.ion.nsearcher.search.SearchResponse;
import net.ion.radon.core.annotation.DefaultValue;
import net.ion.radon.core.annotation.FormParam;
import net.ion.radon.core.annotation.PathParam;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;

public class ListLet extends SearchResource {

	@Get
	public Representation listInfo(@DefaultValue("0") @FormParam("skip") int skip, @DefaultValue("100") @FormParam("offset") int offset, 
				@DefaultValue("html") @PathParam("format") String format) throws Exception {

		SearchRequest sreq = SearchRequest.create("").skip(skip).offset(offset) ;
		SearchResponse response = getSearcher().search(sreq);
		
		List<MyDocument> docs = response.getDocument() ;
		Class clz = Class.forName("net.ion.nsearcher.rest.formater.Search" + format.toUpperCase() + "Formater");
		SearchDocumentFormater af = (SearchDocumentFormater) clz.newInstance();
		
		return  new RopeRepresentation(af.toRope(docs), af.getMediaType()); 
	}

}