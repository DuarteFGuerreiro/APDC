package pt.unl.fct.di.app.avaliacao.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;


import pt.unl.fct.di.app.avaliacao.util.*;


import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;

import java.util.logging.Logger;

@Path("/logout")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LogoutResource {

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());

	public LogoutResource() {
	}

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doLogout(LogoutData data) {
		LOG.fine("Logout attempt by user: " + data.getUsername());

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.getUsername());
		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.getTokenID());
		Transaction txn = datastore.newTransaction();
		try {
			Entity user = txn.get(userKey);
			Entity token = txn.get(tokenKey);
			if (user == null) {
				LOG.warning("Failed user logout for username: " + data.getUsername());
				txn.rollback();
				return Response.status(Status.FORBIDDEN).build();
			}
			if(user.getString("user_state").equals("DISABLED")) {
				LOG.warning("Failed logout for username: " + data.getUsername() + " user was disabled.");
				return Response.status(Status.FORBIDDEN).build();	
			}
			if (token == null) {
				LOG.warning("Failed token logout for username: " + data.getUsername());
				txn.rollback();
				return Response.status(Status.FORBIDDEN).build();
			}
			if (!token.getString("token_username").equals(data.getUsername())) {
				LOG.warning("Failed logout for username: " + data.getUsername() + " wrong username for this token.");
				txn.rollback();
				return Response.status(Status.FORBIDDEN).build();
			}
			if (token.getLong("token_expirationData") < System.currentTimeMillis()) {
				LOG.warning("Failed logout for username: " + data.getUsername() + " token expired.");
				txn.rollback();
				return Response.status(Status.FORBIDDEN).build();
			}
			token = Entity.newBuilder(tokenKey).set("tokenId", token.getString("token_tokenId"))
					.set("token_expirationData", System.currentTimeMillis())
					.set("token_creationData", token.getLong("token_creationData"))
					.set("token_username", token.getString("token_username")).build();
			txn.put(token);
			LOG.info(data.getUsername() + " logged out.");
			txn.commit();
			return Response.ok("{}").build();
		} finally {
			if (txn.isActive())
				txn.rollback();
		}
	}
}