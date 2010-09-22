/*******************************************************************************
 * Copyright (c) 2010 Angelo Zerr and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo Zerr <angelo.zerr@gmail.com> - Initial API and implementation 
 *******************************************************************************/
package org.eclipse.jst.server.jetty.core.internal.jetty70;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.core.internal.ProgressUtil;
import org.eclipse.jst.server.jetty.core.JettyPlugin;
import org.eclipse.jst.server.jetty.core.internal.JettyConfiguration;
import org.eclipse.jst.server.jetty.core.internal.JettyConstants;
import org.eclipse.jst.server.jetty.core.internal.Messages;
import org.eclipse.jst.server.jetty.core.internal.Trace;
import org.eclipse.jst.server.jetty.core.internal.WebModule;
import org.eclipse.jst.server.jetty.core.internal.xml.jetyy70.ServerInstance;
import org.eclipse.jst.server.jetty.core.internal.xml.jetyy70.server.Connector;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.ServerPort;

public class Jetty70Configuration extends JettyConfiguration implements
		JettyConstants {

	protected ServerInstance serverInstance;
	private boolean isServerDirty;
	// property change listeners
	private transient List<PropertyChangeListener> propertyListeners;

	public Collection<ServerPort> getServerPorts() {
		List<ServerPort> ports = new ArrayList<ServerPort>();

		// first add server port
		// try {
		// int port = Integer.parseInt(server.getPort());
		// ports.add(new ServerPort("server", Messages.portServer, port,
		// "TCPIP"));
		// } catch (Exception e) {
		// // ignore
		// }

		// add connectors
		try {

			Collection<Connector> connectors = serverInstance.getConnectors();
			if (connectors != null) {
				for (Connector connector : connectors) {
					int port = -1;
					try {
						port = Integer.parseInt(connector.getPort());
					} catch (Exception e) {
						// ignore
					}
					ports.add(new ServerPort("server", Messages.portServer,
							port, HTTP));
					// TODO : how get HTTP type port???

					// ports.add(new ServerPort(portId, name, port, protocol2,
					// contentTypes, advanced));
				}
			}

		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error getting server ports", e);
		}
		return ports;

		// String instanceServiceName = serverInstance.getService().getName();
		// int size = server.getServiceCount();
		// for (int i = 0; i < size; i++) {
		// Service service = server.getService(i);
		// int size2 = service.getConnectorCount();
		// for (int j = 0; j < size2; j++) {
		// Connector connector = service.getConnector(j);
		// String name = "HTTP/1.1";
		// String protocol2 = "HTTP";
		// boolean advanced = true;
		// String[] contentTypes = null;
		// int port = -1;
		// try {
		// port = Integer.parseInt(connector.getPort());
		// } catch (Exception e) {
		// // ignore
		// }
		// String protocol = connector.getProtocol();
		// if (protocol != null && protocol.length() > 0) {
		// if (protocol.startsWith("HTTP")) {
		// name = protocol;
		// }
		// else if (protocol.startsWith("AJP")) {
		// name = protocol;
		// protocol2 = "AJP";
		// }
		// else {
		// // Get Tomcat equivalent name if protocol handler class specified
		// name = (String)protocolHandlerMap.get(protocol);
		// if (name != null) {
		// // Prepare simple protocol string for ServerPort protocol
		// int index = name.indexOf('/');
		// if (index > 0)
		// protocol2 = name.substring(0, index);
		// else
		// protocol2 = name;
		// }
		// // Specified protocol is unknown, just use as is
		// else {
		// name = protocol;
		// protocol2 = protocol;
		// }
		// }
		// }
		// if (protocol2.toLowerCase().equals("http"))
		// contentTypes = new String[] { "web", "webservices" };
		// String secure = connector.getSecure();
		// if (secure != null && secure.length() > 0) {
		// name = "SSL";
		// protocol2 = "SSL";
		// } else
		// advanced = false;
		// String portId;
		// if (instanceServiceName != null &&
		// instanceServiceName.equals(service.getName()))
		// portId = Integer.toString(j);
		// else
		// portId = i +"/" + j;
		// ports.add(new ServerPort(portId, name, port, protocol2, contentTypes,
		// advanced));
		// }

	}

	/**
	 * Save the information held by this object to the given directory.
	 * 
	 * @param folder
	 *            a folder
	 * @param monitor
	 *            a progress monitor
	 * @throws CoreException
	 */
	public void save(IFolder folder, IProgressMonitor monitor)
			throws CoreException {
		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(Messages.savingTask, 1200);
			if (monitor.isCanceled())
				return;
			monitor.done();
		} catch (Exception e) {
			Trace.trace(
					Trace.SEVERE,
					"Could not save Jetty v7.0 configuration to "
							+ folder.toString(), e);
			throw new CoreException(new Status(IStatus.ERROR,
					JettyPlugin.PLUGIN_ID, 0, NLS.bind(
							Messages.errorCouldNotSaveConfiguration,
							new String[] { e.getLocalizedMessage() }), e));
		}
	}

	public List<WebModule> getWebModules() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addWebModule(int i, WebModule module) {
		// TODO Auto-generated method stub

	}

	/**
	 * Removes a web module.
	 * 
	 * @param index
	 *            int
	 */
	public void removeWebModule(int index) {
		try {
			serverInstance.removeContext(index);
			isServerDirty = true;
			firePropertyChangeEvent(REMOVE_WEB_MODULE_PROPERTY, null, index);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error removing module ref " + index, e);
		}
	}

	protected void firePropertyChangeEvent(String propertyName,
			Object oldValue, Object newValue) {
		if (propertyListeners == null)
			return;

		PropertyChangeEvent event = new PropertyChangeEvent(this, propertyName,
				oldValue, newValue);
		try {
			Iterator<PropertyChangeListener> iterator = propertyListeners
					.iterator();
			while (iterator.hasNext()) {
				try {
					PropertyChangeListener listener = iterator.next();
					listener.propertyChange(event);
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE,
							"Error firing property change event", e);
				}
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error in property event", e);
		}
	}

	/**
	 * Adds a property change listener to this server.
	 * 
	 * @param listener
	 *            java.beans.PropertyChangeListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		if (propertyListeners == null)
			propertyListeners = new ArrayList<PropertyChangeListener>();
		propertyListeners.add(listener);
	}

	/**
	 * Removes a property change listener from this server.
	 * 
	 * @param listener
	 *            java.beans.PropertyChangeListener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		if (propertyListeners != null)
			propertyListeners.remove(listener);
	}

	/**
	 * @see JettyConfiguration#load(IPath, IProgressMonitor)
	 */
	public void load(IPath path, IProgressMonitor monitor) throws CoreException {
		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(Messages.loadingTask, 5);

			// check for catalina.policy to verify that this is a v4.0 config
			// InputStream in = new
			// FileInputStream(path.append("catalina.policy").toFile());
			// in.read();
			// in.close();
			monitor.worked(1);

			// serverFactory = new Factory();
			// serverFactory
			// .setPackageName("org.eclipse.jst.server.jetty.core.internal.xml.server70");
			// server = (Server) serverFactory.loadDocument(new FileInputStream(
			// path.append("jetty.xml").toFile()));
			// serverInstance = new ServerInstance(server);
			// monitor.worked(1);
			//
			// webAppDocument = new
			// WebAppDocument(path.append("webdefault.xml"));
			// monitor.worked(1);

			// jettyUsersDocument = XMLUtil.getDocumentBuilder().parse(new
			// InputSource(new
			// FileInputStream(path.append("jetty-users.xml").toFile())));
			monitor.worked(1);

			// load policy file
			// policyFile = JettyVersionHelper.getFileContents(new
			// FileInputStream(path.append("catalina.policy").toFile()));
			monitor.worked(1);

			if (monitor.isCanceled())
				return;
			monitor.done();
		} catch (Exception e) {
			Trace.trace(
					Trace.WARNING,
					"Could not load Jetty v7.0 configuration from "
							+ path.toOSString() + ": " + e.getMessage());
			throw new CoreException(new Status(IStatus.ERROR,
					JettyPlugin.PLUGIN_ID, 0, NLS.bind(
							Messages.errorCouldNotLoadConfiguration,
							path.toOSString()), e));
		}
	}

	public void load(IFolder folder, IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub

	}

}