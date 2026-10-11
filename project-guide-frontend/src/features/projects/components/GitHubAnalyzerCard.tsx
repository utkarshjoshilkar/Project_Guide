import React, { useState } from 'react';
import { GitBranch, Loader2, CheckCircle, AlertCircle, Link as LinkIcon, RefreshCw, Lightbulb } from 'lucide-react';
import { githubService, ProgressReport, AdaptiveGuidanceResponse } from '@/services/githubService';

export const GitHubAnalyzerCard = ({ projectId }: { projectId: number }) => {
  const [repoUrl, setRepoUrl] = useState('');
  const [isConnecting, setIsConnecting] = useState(false);
  const [isVerifying, setIsVerifying] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [report, setReport] = useState<ProgressReport | null>(null);
  const [guidance, setGuidance] = useState<AdaptiveGuidanceResponse | null>(null);
  const [isGettingGuidance, setIsGettingGuidance] = useState(false);

  const handleConnect = async () => {
    if (!repoUrl) {
      setError('Please enter a repository URL');
      return;
    }
    
    setIsConnecting(true);
    setError(null);
    try {
      // Step 1: Connect
      await githubService.connectRepository(projectId, repoUrl);
      // Step 2: Acquire
      await githubService.acquireRepository(projectId);
      setRepoUrl(''); // Clear on success
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to connect repository.');
    } finally {
      setIsConnecting(false);
    }
  };

  const handleVerify = async () => {
    setIsVerifying(true);
    setError(null);
    try {
      const data = await githubService.verifyProgress(projectId);
      setReport(data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to verify progress. Did you connect the repository first?');
    } finally {
      setIsVerifying(false);
    }
  };

  const handleGetGuidance = async () => {
    setIsGettingGuidance(true);
    setError(null);
    try {
      const data = await githubService.getGuidance(projectId);
      setGuidance(data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to get guidance.');
    } finally {
      setIsGettingGuidance(false);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'IMPLEMENTED': return 'text-emerald-400 bg-emerald-400/10 border-emerald-400/20';
      case 'PARTIALLY_IMPLEMENTED': return 'text-amber-400 bg-amber-400/10 border-amber-400/20';
      case 'NOT_IMPLEMENTED': return 'text-red-400 bg-red-400/10 border-red-400/20';
      default: return 'text-gray-400 bg-gray-400/10 border-gray-400/20';
    }
  };

  return (
    <div className="glass-card p-6 md:p-8 mt-6">
      <div className="flex items-center gap-3 mb-6">
        <GitBranch className="text-white" size={24} />
        <h2 className="text-xl font-bold text-white">GitHub Analyzer</h2>
      </div>

      {error && (
        <div className="mb-6 p-4 rounded-lg bg-red-500/10 border border-red-500/20 text-red-400 flex items-start gap-3">
          <AlertCircle size={20} className="shrink-0 mt-0.5" />
          <p className="text-sm">{error}</p>
        </div>
      )}

      {/* Connection Form */}
      <div className="flex flex-col sm:flex-row gap-3 mb-8">
        <div className="flex-1 relative">
          <div className="absolute inset-y-0 left-3 flex items-center pointer-events-none">
            <LinkIcon size={16} className="text-text-muted" />
          </div>
          <input
            type="url"
            value={repoUrl}
            onChange={(e) => setRepoUrl(e.target.value)}
            placeholder="https://github.com/username/repo"
            className="w-full pl-10 pr-4 py-2 bg-surface-dark border border-white/10 rounded-lg focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-colors text-white text-sm"
          />
        </div>
        <button
          onClick={handleConnect}
          disabled={isConnecting}
          className="px-6 py-2 bg-primary hover:bg-primary-light text-white rounded-lg flex items-center justify-center gap-2 transition-colors font-medium shadow-sm disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {isConnecting ? <Loader2 size={18} className="animate-spin" /> : <LinkIcon size={18} />}
          Connect Repo
        </button>
      </div>

      <div className="border-t border-white/5 pt-6">
        <div className="flex items-center justify-between mb-6">
          <p className="text-sm text-text-muted">Analyze your code to track milestone progress.</p>
          <div className="flex gap-2">
            <button
              onClick={handleVerify}
              disabled={isVerifying}
              className="px-4 py-2 bg-surface border border-white/10 hover:bg-surface-light text-white rounded-lg flex items-center gap-2 transition-colors text-sm"
            >
              {isVerifying ? <Loader2 size={16} className="animate-spin" /> : <RefreshCw size={16} />}
              {isVerifying ? 'Analyzing...' : 'Verify Progress'}
            </button>
            <button
              onClick={handleGetGuidance}
              disabled={isGettingGuidance}
              className="px-4 py-2 bg-primary/20 border border-primary/50 hover:bg-primary/30 text-primary-light rounded-lg flex items-center gap-2 transition-colors text-sm"
            >
              {isGettingGuidance ? <Loader2 size={16} className="animate-spin" /> : <Lightbulb size={16} />}
              {isGettingGuidance ? 'Getting Guidance...' : 'Get Guidance'}
            </button>
          </div>
        </div>

        {/* Guidance Results */}
        {guidance && guidance.recommendedActions.length > 0 && (
          <div className="space-y-4 mb-8">
            <h3 className="text-lg font-semibold text-white mb-3 flex items-center gap-2">
              <Lightbulb className="text-primary" size={20} />
              AI Guidance
            </h3>
            <div className="space-y-3">
              {guidance.recommendedActions.map((action, idx) => (
                <div key={idx} className="bg-primary/5 border border-primary/20 rounded-lg p-4">
                  <div className="flex justify-between items-start mb-2">
                    <h4 className="font-medium text-white">{action.taskTitle}</h4>
                    <span className="text-xs font-semibold px-2 py-1 bg-primary/10 text-primary rounded-full">
                      {action.actionType}
                    </span>
                  </div>
                  <div className="text-sm text-text-muted whitespace-pre-wrap mt-2">{action.explanation}</div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Results */}
        {report && (
          <div className="space-y-4">
            <h3 className="text-lg font-semibold text-white mb-3">Analysis Results</h3>
            {report.featureVerifications.length === 0 ? (
              <p className="text-text-muted text-sm italic">No features verified yet.</p>
            ) : (
              report.featureVerifications.map((verification, idx) => (
                <div key={idx} className="bg-surface-dark border border-white/5 rounded-lg p-4">
                  <div className="flex items-start justify-between gap-4 mb-2">
                    <h4 className="font-medium text-white">{verification.expectedTask.title}</h4>
                    <span className={`px-2.5 py-0.5 rounded-full text-xs font-medium border ${getStatusColor(verification.status)}`}>
                      {verification.status.replace(/_/g, ' ')}
                    </span>
                  </div>
                  <p className="text-sm text-text-muted mb-3">{verification.reasoning}</p>
                  
                  {verification.evidence?.signals && verification.evidence.signals.length > 0 && (
                    <div className="mt-3 pt-3 border-t border-white/5">
                      <p className="text-xs font-semibold text-text-muted uppercase tracking-wider mb-2">Evidence Found</p>
                      <ul className="space-y-2">
                        {verification.evidence.signals.map((signal, sIdx) => (
                          <li key={sIdx} className="text-xs text-text-muted flex items-start gap-2">
                            <CheckCircle size={14} className="text-emerald-500 shrink-0 mt-0.5" />
                            <span>
                              <span className="text-white">{signal.type}: </span> 
                              {signal.description} 
                              <span className="opacity-50 ml-1">({signal.filePath})</span>
                            </span>
                          </li>
                        ))}
                      </ul>
                    </div>
                  )}
                </div>
              ))
            )}
          </div>
        )}
      </div>
    </div>
  );
};
