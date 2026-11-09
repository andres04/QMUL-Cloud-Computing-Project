package qmul.marketplace.data

import org.koin.core.module.Module

/**
 * Platform-specific Koin module declaration (Room DB & repository bindings on supported platforms).
 */
expect val platformModule: Module
