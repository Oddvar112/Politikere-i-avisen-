package folkestad.project.TextSummarizer;

public class SummaryResult {
    private final String summary;
    private final int originalWordCount;
    private final int summaryWordCount;
    private final double compressionRatio;

    public SummaryResult(String summary, int originalWordCount, int summaryWordCount, double compressionRatio) {
        this.summary = summary;
        this.originalWordCount = originalWordCount;
        this.summaryWordCount = summaryWordCount;
        this.compressionRatio = compressionRatio;
    }

    public String getSummary() { return summary; }
    public int getOriginalWordCount() { return originalWordCount; }
    public int getSummaryWordCount() { return summaryWordCount; }
    public double getCompressionRatio() { return compressionRatio; }
}
