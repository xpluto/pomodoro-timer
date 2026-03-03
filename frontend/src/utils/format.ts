/**
 * Generates a default task name using the current local time.
 * Example: "Focus @ 14:15"
 */
export function generateDefaultTaskName(): string {
  const now = new Date()
  const hh = String(now.getHours()).padStart(2, '0')
  const mm = String(now.getMinutes()).padStart(2, '0')
  return `Focus @ ${hh}:${mm}`
}

/**
 * Converts an ISO datetime string to "HH:mm" local time.
 */
export function formatTime(isoString: string): string {
  const date = new Date(isoString)
  const hh = String(date.getHours()).padStart(2, '0')
  const mm = String(date.getMinutes()).padStart(2, '0')
  return `${hh}:${mm}`
}

/**
 * Formats a duration in seconds to a human-readable string.
 * Examples: "25min", "1h 30min"
 */
export function formatDuration(seconds: number): string {
  const totalMinutes = Math.round(seconds / 60)
  const h = Math.floor(totalMinutes / 60)
  const m = totalMinutes % 60
  return h > 0 ? `${h}h ${m}min` : `${m}min`
}
