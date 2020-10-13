package com.blazemeter.jmeter.citrix.recorder;

import com.blazemeter.jmeter.citrix.utils.CitrixUtils;
import org.apache.jmeter.engine.TreeCloner;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.timers.Timer;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.visualizers.backend.Backend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a {@link TreeCloner} that disables all Thread Groups.
 */
public class TreeClonerForICADownloading extends TreeCloner {

  private static final Logger LOGGER = LoggerFactory.getLogger(TreeClonerForICADownloading.class);

  private static final boolean ICA_DOWNLOADING_IGNORE_TIMERS = JMeterUtils
      .getPropDefault(CitrixUtils.PROPERTIES_PFX + "ica_downloading_ignore_timers", true);
  //$NON-NLS-1$

  private static final boolean ICA_DOWNLOADING_IGNORE_BACKENDS = JMeterUtils
      .getPropDefault(CitrixUtils.PROPERTIES_PFX + "ica_downloading_ignore_backends", true);
  //$NON-NLS-1$

  static {
    LOGGER.info("Running ICA file downloading with ignoreTimers:{}, ignoreBackends:{}",
        ICA_DOWNLOADING_IGNORE_TIMERS, ICA_DOWNLOADING_IGNORE_BACKENDS);
  }


  /**
   * Instantiates a new {@link TreeClonerForICADownloading}.
   */
  public TreeClonerForICADownloading() {
    this(false);
  }

  /**
   * Instantiates a new {@link TreeClonerForICADownloading}.
   *
   * @param honourNoThreadClone indicates whether NoThreadClone is honoured
   *                            during cloning
   */
  public TreeClonerForICADownloading(boolean honourNoThreadClone) {
    super(honourNoThreadClone);
  }

  @Override
  protected Object addNodeToTree(Object node) {
    // depending on jmeter properties, don't add timers or backends
    if ((ICA_DOWNLOADING_IGNORE_TIMERS && node instanceof Timer)
        || (ICA_DOWNLOADING_IGNORE_BACKENDS && node instanceof Backend)) {
      return node;
    } else {
      Object clonedNode = super.addNodeToTree(node);

      // Initialize cloned thread groups
      if (clonedNode instanceof ThreadGroup) {
        ThreadGroup threadGroup = (ThreadGroup) clonedNode;
        // No execution for others
        threadGroup.setNumThreads(0);
      }
      return clonedNode;
    }
  }

}
