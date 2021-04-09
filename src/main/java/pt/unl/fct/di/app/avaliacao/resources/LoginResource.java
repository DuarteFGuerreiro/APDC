package pt.unl.fct.di.app.avaliacao.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;

import com.google.gson.*;

import pt.unl.fct.di.app.avaliacao.util.*;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;

import java.util.logging.Logger;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LoginResource {

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Gson g = new Gson();

	public LoginResource() {
	}

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doLogin(LoginData data) {
		LOG.fine("Login attempt by user: " + data.getUsername());

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.getUsername());
		Transaction txn = datastore.newTransaction();
		try {
			Entity user = datastore.get(userKey);
			if (user == null) {
				LOG.warning("Failed login for username: " + data.getUsername());
				return Response.status(Status.FORBIDDEN).build();
			}
			if(user.getString("user_state").equals("DISABLED")) {
				LOG.warning("Failed login for username: " + data.getUsername() + " user was disabled.");
				return Response.status(Status.FORBIDDEN).build();	
			}
			String hasherPass = user.getString("user_password");
			if (hasherPass.equals(DigestUtils.sha512Hex(data.getPassword()))) {
				AuthToken aux = new AuthToken(data.getUsername());
				Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(aux.tokenID);
				Entity token = txn.get(tokenKey);
				token = Entity.newBuilder(tokenKey).set("token_username", data.getUsername())
						.set("token_tokenId", aux.tokenID)
						.set("token_creationData", aux.creationData)
						.set("token_expirationData", aux.expirationData).build();
				txn.add(token);
				LOG.info("Token created for " + data.getUsername());
				txn.commit();
				return Response.ok(g.toJson(token)).build();
			}
		} finally {
			if (txn.isActive())
				txn.rollback();
		}
		return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
	}
}