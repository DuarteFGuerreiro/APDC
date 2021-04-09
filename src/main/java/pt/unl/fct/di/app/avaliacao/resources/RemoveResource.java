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

@Path("/remove")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RemoveResource {

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());

	public RemoveResource() {
	}

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doRemove(RemoveData data) {
		LOG.fine("Remove account attempt for user: " + data.getUsername());

		Key adminKey = datastore.newKeyFactory().setKind("User").newKey(data.getAdmin());
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.getUsername());
		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.getTokenID());
		Transaction txn = datastore.newTransaction();
		try {
			Entity user = txn.get(userKey);
			Entity token = txn.get(tokenKey);
			Entity admin = txn.get(adminKey);
			if (user == null) {
				LOG.warning("Failed remove for username: " + data.getUsername());
				txn.rollback();
				return Response.status(Status.FORBIDDEN).build();
			}
			if(!user.getString("user_role").equals("USER")) {
				LOG.warning("Failed remove for username: " + data.getUsername() + " only users can be removed.");
				txn.rollback();
				return Response.status(Status.FORBIDDEN).build();	
			}
			if(admin.getString("user_role").equals("SU")) {	
				LOG.warning("Failed remove for username: " + data.getUsername() + " SU can not remove users.");
				txn.rollback();
				return Response.status(Status.FORBIDDEN).build();	
			}
			if(user.getString("user_state").equals("DISABLED")) {
				LOG.warning("Failed login for username: " + data.getUsername() + " user was disabled.");
				return Response.status(Status.FORBIDDEN).build();	
			}
			if (token == null) {
				LOG.warning("Failed token remove for username: " + data.getUsername());
				txn.rollback();
				return Response.status(Status.FORBIDDEN).build();
			}
			if (!token.getString("token_username").equals(data.getAdmin())) {
				LOG.warning("Failed remove for username: " + data.getUsername() + " wrong username for this token.");
				txn.rollback();
				return Response.status(Status.FORBIDDEN).build();
			}
			if (user.getString("removed").equals("removed")) {
				LOG.warning("Failed logout for username: " + data.getUsername() + " user removed.");
				txn.rollback();
				return Response.status(Status.FORBIDDEN).build();	
			}
			if(token.getLong("token_expirationData")<System.currentTimeMillis()) {
				LOG.warning("Failed remove for username: " + data.getUsername() + " expired token.");
				txn.rollback();
				return Response.status(Status.FORBIDDEN).build();	
			}
			user = Entity.newBuilder(userKey).set("user_username", user.getString("user_username"))
					.set("user_password", user.getString("user_password"))
					.set("user_email", user.getString("user_email"))
					.set("user_role", user.getString("user_role"))
					.set("user_state", user.getString("user_state"))
					.set("user_address1", user.getString("user_address1"))
					.set("user_address2", user.getString("user_address2"))
					.set("user_phone", user.getString("user_phone"))
					.set("user_mobile", user.getString("user_mobile"))
					.set("user_locality", user.getString("user_locality"))
					.set("user_profile", user.getString("user_profile"))
					.set("removed", "removed")
					.build();
			txn.put(user);
			LOG.info("Removed " + data.getUsername());
			txn.commit();
			return Response.ok("{}").build();
		} finally {
			if (txn.isActive())
				txn.rollback();
		}
	}
}