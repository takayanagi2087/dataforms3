interface Page {
	contextPath: string;
	errorPage: string;
	csrfToken?: string | null;
	useUniqueId: boolean;
	contextPath: string;
	lock(): void;
	unlock(): void;
	alert?(title: unknown, message: string): void | Promise<void>;
}

interface Logger {
	log(message: string, ...values: unknown[]): void;
	info(message: string, ...values: unknown[]): void;
	warn(message: string, ...values: unknown[]): void;
	error(message: string, ...values: unknown[]): void;
}


declare global {
	interface Window {
		currentPage: Page;
		logger: Logger;
	}
	const currentPage: Page;
	const logger: Logger;
}


export {}