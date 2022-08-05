export interface ChartRenderer {
  renderChart(data: any, config: any): Promise<any>;
  toImage(event: any): void;
  destroy(): void;
}
