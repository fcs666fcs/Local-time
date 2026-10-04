# Local Time UI Redesign 1.1

This pass implements the visual/interaction layer from the product document rather than a minimal Compose demo.

## Visual direction

- Material 3 information hierarchy.
- Liquid-glass-inspired translucent surfaces with layered gradients, soft borders, and highlight passes.
- Cool neutral background with warm solar accent; the sun accent is not used for every state.
- Light/Dark/System theme modes.
- Compact floating glass navigation with five required destinations: Home, Calculator, Sky, Geography, Settings.

## Home

- Current place and location source.
- Civil/zone time, high-school geographic local time, longitude delta.
- Large live local-time readout.
- Real-time solar trajectory visualization.
- Solar altitude/azimuth.
- Mean solar time and apparent solar time.
- Sunrise/noon/sunset and polar states.
- Solar declination / subsolar-latitude explanation.

## Calculator

- Geographic local time.
- Longitude difference.
- Daylight duration / sunrise / sunset.
- Time-zone central meridian.
- Step-by-step calculations instead of answer-only output.

## Sky

- Date simulation.
- Continuous time slider.
- Solar trajectory.
- Solar altitude, azimuth, declination.
- Sunrise/sunset.
- Subsolar latitude explanation.

## Geography

- 2D Earth teaching diagram.
- Day/night indication.
- Terminator curve.
- Observer point.
- Subsolar latitude and polar-day/polar-night explanation.

## Settings

- System/Light/Dark theme.
- Reduced-motion switch.
- Location management entry.
- Offline/privacy explanation.
- About/version.

## Location

- Fine/coarse location permissions only when the user requests GPS.
- Last/current fused location lookup.
- Manual latitude/longitude entry remains available when permission is denied.
- Validation follows the documented ranges: latitude [-90, 90], longitude [-180, 180].

## Performance

The UI keeps the calculation core local and does not introduce a network call into the first frame. Continuous time refresh is limited to a one-second clock tick, while the trajectory is memoized by date and latitude.
