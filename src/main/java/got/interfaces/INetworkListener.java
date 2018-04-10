package got.interfaces;

import com.esotericsoftware.kryonet.Connection;

import got.server.GameServer.PlayerConnection;

/**
 * @author Souverain73
 *	Network listener must handle network packages
 *	All Game states implements this interface for network communication
 */
public interface INetworkListener {
	/**This method reacts on network package.
	 * @param connection network connection
	 * @param pkg network package
	 */
	void recieve(Connection connection, Object pkg);
}
