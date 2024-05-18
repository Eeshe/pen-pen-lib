package me.eeshe.penpenlib.models;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.eeshe.penpenlib.util.FoliaUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;

public final class Scheduler {

    /**
     * Runs passed runnable for both Folia and Bukkit synchronously.
     *
     * @param plugin   Plugin calling the function.
     * @param runnable Code that will be executed.
     */
    public static void run(Plugin plugin, Runnable runnable) {
        run(plugin, null, runnable, false);
    }

    /**
     * Runs passed runnable for both Folia and Bukkit asynchronously.
     *
     * @param plugin   Plugin calling the function.
     * @param runnable Code that will be executed.
     */
    public static void runAsync(Plugin plugin, Runnable runnable) {
        run(plugin, null, runnable, true);
    }

    /**
     * Runs passed runnable for both Folia and Bukkit synchronously.
     *
     * @param plugin   Plugin calling the function.
     * @param location Location the runnable will be executed in.
     * @param runnable Code that will be executed.
     */
    public static void run(Plugin plugin, Location location, Runnable runnable) {
        run(plugin, location, runnable, false);
    }

    /**
     * Runs passed runnable for both Folia and Bukkit asynchronously.
     *
     * @param plugin   Plugin calling the function.
     * @param location Location the runnable will be executed in.
     * @param runnable Code that will be executed.
     */
    public static void runAsync(Plugin plugin, Location location, Runnable runnable) {
        run(plugin, location, runnable, true);
    }

    /**
     * Runs passed runnable for both Folia and Bukkit.
     *
     * @param plugin   Plugin calling the function.
     * @param location Location the runnable will be executed in.
     * @param runnable Code that will be executed.
     * @param async    Whether the runnable should be executed asynchronously.
     */
    private static void run(Plugin plugin, Location location, Runnable runnable, boolean async) {
        if (!FoliaUtil.isFolia()) {
            if (!async) {
                Bukkit.getScheduler().runTask(plugin, runnable);
            } else {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
            }
        } else {
            if (async) {
                Bukkit.getAsyncScheduler().runNow(plugin, scheduledTask -> runnable.run());
            } else if (location == null) {
                Bukkit.getGlobalRegionScheduler().execute(plugin, runnable);
            } else {
                Bukkit.getRegionScheduler().execute(plugin, location, runnable);
            }
        }
    }

    /**
     * Runs passed runnable for both Folia and Bukkit with the specified delay synchronously.
     *
     * @param plugin     Plugin calling the function.
     * @param runnable   Code that will be executed.
     * @param delayTicks Delay in ticks.
     */
    public static Task runLater(Plugin plugin, Runnable runnable, long delayTicks) {
        return runLater(plugin, null, runnable, delayTicks, false);
    }

    /**
     * Runs passed runnable for both Folia and Bukkit with the specified delay asynchronously.
     *
     * @param plugin     Plugin calling the function.
     * @param runnable   Code that will be executed.
     * @param delayTicks Delay in ticks.
     */
    public static Task runLaterAsync(Plugin plugin, Runnable runnable, long delayTicks) {
        return runLater(plugin, null, runnable, delayTicks, true);
    }

    /**
     * Runs passed runnable for both Folia and Bukkit with the specified delay synchronously.
     *
     * @param plugin     Plugin calling the function.
     * @param location   Location the runnable will be executed in.
     * @param runnable   Code that will be executed.
     * @param delayTicks Delay in ticks.
     */
    public static Task runLater(Plugin plugin, Location location, Runnable runnable, long delayTicks) {
        return runLater(plugin, location, runnable, delayTicks, false);
    }

    /**
     * Runs passed runnable for both Folia and Bukkit with the specified delay asynchronously.
     *
     * @param plugin     Plugin calling the function.
     * @param location   Location the runnable will be executed in.
     * @param runnable   Code that will be executed.
     * @param delayTicks Delay in ticks.
     */
    public static Task runLaterAsync(Plugin plugin, Location location, Runnable runnable, long delayTicks) {
        return runLater(plugin, location, runnable, delayTicks, true);
    }

    /**
     * Runs passed runnable for both Folia and Bukkit after a passed delay.
     *
     * @param plugin     Plugin calling the function.
     * @param location   Location the runnable will be executed in.
     * @param runnable   Code that will be executed.
     * @param delayTicks Delay in ticks.
     * @param async      Whether the runnable should be executed asynchronously.
     */
    private static Task runLater(Plugin plugin, Location location, Runnable runnable, long delayTicks, boolean async) {
        if (!FoliaUtil.isFolia()) {
            if (!async) {
                return new Task(Bukkit.getScheduler().runTaskLater(plugin, runnable, delayTicks));
            } else {
                return new Task(Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delayTicks));
            }
        } else {
            delayTicks = Math.max(1, delayTicks);
            long delayMillis = Math.max(1, delayTicks * 50);
            if (async) {
                return new Task(Bukkit.getAsyncScheduler().runDelayed(plugin, t -> runnable.run(), delayMillis, TimeUnit.MILLISECONDS));
            } else if (location == null) {
                return new Task(Bukkit.getGlobalRegionScheduler().runDelayed(plugin, t -> runnable.run(), delayTicks));
            } else {
                return new Task(Bukkit.getRegionScheduler().runDelayed(plugin, location, t -> runnable.run(), delayTicks));
            }
        }
    }

    /**
     * Runs passed runnable for both Folia and Bukkit with the specified frequency synchronously.
     *
     * @param plugin         Plugin calling the function.
     * @param runnable       Code that will be executed.
     * @param delayTicks     Delay of the first execution in ticks.
     * @param frequencyTicks Frequency of execution in ticks.
     */
    public static Task runTimer(Plugin plugin, Runnable runnable, long delayTicks, long frequencyTicks) {
        return runTimer(plugin, null, runnable, delayTicks, frequencyTicks, false);
    }

    /**
     * Runs passed runnable for both Folia and Bukkit with the specified frequency asynchronously.
     *
     * @param plugin         Plugin calling the function.
     * @param runnable       Code that will be executed.
     * @param delayTicks     Delay of the first execution in ticks.
     * @param frequencyTicks Frequency of execution in ticks.
     */
    public static Task runTimerAsynchronously(Plugin plugin, Runnable runnable, long delayTicks, long frequencyTicks) {
        return runTimer(plugin, null, runnable, delayTicks, frequencyTicks, true);
    }

    /**
     * Runs passed runnable for both Folia and Bukkit with the specified frequency asynchronously.
     *
     * @param plugin         Plugin calling the function.
     * @param location       Location the runnable will be executed in.
     * @param runnable       Code that will be executed.
     * @param delayTicks     Delay of the first execution in ticks.
     * @param frequencyTicks Frequency of execution in ticks.
     */
    public static Task runTimer(Plugin plugin, Location location, Runnable runnable, long delayTicks,
                                long frequencyTicks) {
        return runTimer(plugin, location, runnable, delayTicks, frequencyTicks, false);
    }

    /**
     * Runs passed runnable for both Folia and Bukkit with the specified frequency synchronously.
     *
     * @param plugin         Plugin calling the function.
     * @param location       Location the runnable will be executed in.
     * @param runnable       Code that will be executed.
     * @param delayTicks     Delay of the first execution in ticks.
     * @param frequencyTicks Frequency of execution in ticks.
     */
    public static Task runTimerAsynchronously(Plugin plugin, Location location, Runnable runnable,
                                              long delayTicks, long frequencyTicks) {
        return runTimer(plugin, location, runnable, delayTicks, frequencyTicks, true);
    }

    /**
     * Runs passed runnable for both Folia and Bukkit after a passed delay.
     *
     * @param plugin         Plugin calling the function.
     * @param location       Location the runnable will be executed in.
     * @param runnable       Code that will be executed.
     * @param delayTicks     Delay in ticks.
     * @param frequencyTicks Frequency of execution in ticks.
     * @param async          Whether the runnable should be executed asynchronously.
     */
    private static Task runTimer(Plugin plugin, Location location, Runnable runnable, long delayTicks,
                                 long frequencyTicks, boolean async) {
        if (!FoliaUtil.isFolia()) {
            if (!async) {
                return new Task(Bukkit.getScheduler().runTaskTimer(plugin, runnable, delayTicks, frequencyTicks));
            } else {
                return new Task(Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delayTicks,
                        frequencyTicks));
            }
        } else {
            delayTicks = Math.max(1, delayTicks);
            frequencyTicks = Math.max(1, frequencyTicks);
            long delayMillis = Math.max(1, delayTicks * 50);
            long frequencyMillis = Math.max(1, frequencyTicks * 50);
            if (async) {
                return new Task(Bukkit.getAsyncScheduler().runAtFixedRate(plugin, t -> runnable.run(), delayMillis,
                        frequencyMillis, TimeUnit.MILLISECONDS));
            } else if (location == null) {
                return new Task(Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, t -> runnable.run(),
                        delayTicks, frequencyTicks));
            } else {
                return new Task(Bukkit.getRegionScheduler().runAtFixedRate(plugin, location, t -> runnable.run(),
                        delayTicks, frequencyTicks));
            }
        }
    }

    public static class Task {

        private Object foliaTask;
        private BukkitTask bukkitTask;

        Task(Object foliaTask) {
            this.foliaTask = foliaTask;
        }

        Task(BukkitTask bukkitTask) {
            this.bukkitTask = bukkitTask;
        }

        public void cancel() {
            if (foliaTask != null)
                ((ScheduledTask) foliaTask).cancel();
            else
                bukkitTask.cancel();
        }

        public boolean isCancelled() {
            if (bukkitTask != null) {
                return bukkitTask.isCancelled();
            } else {
                return ((ScheduledTask) foliaTask).isCancelled();
            }
        }
    }
}