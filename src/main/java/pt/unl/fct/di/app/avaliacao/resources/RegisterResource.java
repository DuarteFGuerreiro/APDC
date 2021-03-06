package pt.unl.fct.di.app.avaliacao.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;

import java.util.logging.Logger;

import pt.unl.fct.di.app.avaliacao.util.RegisterData;

@Path("/register")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RegisterResource {
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doRegistration(RegisterData data) {
		LOG.fine("Attempt to register user: " + data.getUsername());

		if (!data.validRegistration()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}

		Transaction txn = datastore.newTransaction();
		try {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.getUsername());
			Entity user = txn.get(userKey);
			if (user != null) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("User already exists.").build();
			} else {
				user = Entity.newBuilder(userKey).set("user_username", data.getUsername())
						.set("user_password", DigestUtils.sha512Hex(data.getPassword()))
						.set("user_email", data.getEmail())
						.set("user_role", "USER")
						.set("user_profile", "")
						.set("user_phone", "")
						.set("user_mobile", "")
						.set("user_address1", "")
						.set("user_address2", "")
						.set("user_locality", "")
						.set("user_state", "ENABLED")
						.set("removed", "")
						.build();
				txn.add(user);
				LOG.info("User registered " + data.getUsername());
				txn.commit();
				return Response.ok("{}").build();

			}
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
}