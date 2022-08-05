export interface DpIcons {
    time: string,
    date: string,
    up: string,
    down: string,
    next: string,
    previous: string,
}

export interface DpOptions {
    icons: DpIcons,
    defaultDate: string,
    maxDate?: string,
    minDate?: string,
    format?: string
}
