package org.avaje.metric;

import java.util.Collection;
import java.util.Iterator;
import java.util.ServiceLoader;

import org.avaje.metric.spi.PluginMetricManager;

/**
 * Manages the creation and registration of Metrics.
 * <p>
 * Provides methods to allow agents to go through the registered metrics and gather/report the
 * statistics.
 * <p>
 * This uses a service locator to initialise a underlying PluginMetricManager instance. 
 */
public class MetricManager {

  private static final PluginMetricManager mgr = initialiseProvider();

  private static final String DEFAULT_PROVIDER = "org.avaje.metric.core.DefaultMetricManager";

  private static PluginMetricManager initialiseProvider() {

    ServiceLoader<PluginMetricManager> loader = ServiceLoader.load(PluginMetricManager.class);
    Iterator<PluginMetricManager> it = loader.iterator();
    if (it.hasNext()) {
      return it.next();
    }
    try {
      Class<?> clazz = Class.forName(DEFAULT_PROVIDER);
      return (PluginMetricManager) clazz.newInstance();

    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Provider " + DEFAULT_PROVIDER + " not found", e);

    } catch (Exception e) {
      throw new RuntimeException("Provider " + DEFAULT_PROVIDER + " could not be instantiated: " + e, e);
    }
  }

  /**
   * Create a MetricName based on a class and name.
   * <p>
   * Often the name maps to a method name.
   */
  public static MetricName name(Class<?> cls, String name) {
    return mgr.name(cls, name);
  }

  /**
   * Create a Metric name based on group, type and name.
   * 
   * @param group
   *          The group which often maps to a package name.
   * @param type
   *          The type which often maps to a simple class name.
   * @param name
   *          The name which often maps to a method name.
   */
  public static MetricName name(String group, String type, String name) {
    return mgr.name(group, type, name);
  }

  /**
   * Create a Metric name by parsing a name that is expected to include periods (dot notation
   * similar to package.Class.method).
   */
  public static MetricName nameParse(String name) {
    return mgr.nameParse(name);
  }

  /**
   * Return a MetricNameCache for the given class.
   * <p>
   * The MetricNameCache can be used to derive MetricName objects dynamically with relatively less
   * overhead.
   * </p>
   */
  public static MetricNameCache getMetricNameCache(Class<?> cls) {
    return mgr.getMetricNameCache(cls);
  }

  /**
   * Return a MetricNameCache for a given base metric name.
   * <p>
   * The MetricNameCache can be used to derive MetricName objects dynamically with relatively less
   * overhead.
   * </p>
   */
  public static MetricNameCache getMetricNameCache(MetricName baseName) {
    return mgr.getMetricNameCache(baseName);
  }

  /**
   * Return a TimedMetric using the Class, name to derive the MetricName.
   */
  public static TimedMetric getTimedMetric(Class<?> cls, String eventName) {
    return getTimedMetric(name(cls, eventName));
  }

  /**
   * Return a TimedMetric given the name.
   */
  public static TimedMetric getTimedMetric(MetricName name) {
    return mgr.getTimedMetric(name);
  }

  /**
   * Return a TimedMetric given the name.
   */
  public static TimedMetric getTimedMetric(String name) {
    return mgr.getTimedMetric(name);
  }

  /**
   * Return a CounterMetric given the name.
   */
  public static CounterMetric getCounterMetric(MetricName name) {
    return mgr.getCounterMetric(name);
  }

  /**
   * Return a CounterMetric using the Class and name to derive the MetricName.
   */
  public static CounterMetric getCounterMetric(Class<?> cls, String eventName) {
    return getCounterMetric(name(cls, eventName));
  }

  /**
   * Return a ValueMetric using the Class and name to derive the MetricName.
   */
  public static ValueMetric getValueMetric(Class<?> cls, String eventName) {
    return getValueMetric(name(cls, eventName));
  }

  /**
   * Return a ValueMetric given the name.
   */
  public static ValueMetric getValueMetric(MetricName name) {
    return mgr.getValueMetric(name);
  }

  /**
   * Return the TimedMetricGroup with default rateUnit of Minutes and using the default clock.
   */
  public static TimedMetricGroup getTimedMetricGroup(MetricName baseName) {
    return mgr.getTimedMetricGroup(baseName);
  }

  /**
   * Return a TimedMetricGroup with a common group and type name.
   * 
   * @param group
   *          the common group name
   * @param type
   *          the common type name
   *          
   * @return the TimedMetricGroup used to create TimedMetric's that have a common base name.
   */
  public static TimedMetricGroup getTimedMetricGroup(String group, String type) {
    return getTimedMetricGroup(name(group, type, ""));
  }

  /**
   * Create and register a GaugeMetric using the gauge supplied.
   */
  public static GaugeMetric register(MetricName name, Gauge gauge) {
    return mgr.registerGauge(name, gauge);
  }

  /**
   * Create and register a GaugeCounterMetric using the gauge supplied.
   */
  public static GaugeCounterMetric register(MetricName name, GaugeCounter gauge) {
    return mgr.registerGauge(name, gauge);
  }

  /**
   * Return all the non-jvm registered metrics.
   */
  public static Collection<Metric> getMetrics() {
    return mgr.getMetrics();
  }

  /**
   * Return all the non-jvm registered metrics that are not empty.
   */
  public static Collection<Metric> collectNonEmptyMetrics() {
    return mgr.collectNonEmptyMetrics();
  }

  /**
   * Return the core JVM metrics.
   */
  public static Collection<Metric> getJvmMetrics() {
    return mgr.getJvmMetrics();
  }

}