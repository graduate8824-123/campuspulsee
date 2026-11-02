import React, { useState } from 'react';
import {
  Database,
  Layers,
  FileCode2,
  GitBranch,
  Workflow,
  CheckCircle2,
  AlertCircle,
  Play,
  ArrowRight,
  Code2,
  ShieldCheck,
  Search,
  ExternalLink,
  Copy,
  Check,
  Terminal,
  Server,
  UserCheck,
  PlusCircle,
  Tag,
  MapPin,
  Clock,
  ChevronRight,
  RefreshCw,
  Folder,
  FileText
} from 'lucide-react';

interface TabItem {
  id: string;
  label: string;
  icon: React.ComponentType<{ className?: string }>;
  badge?: string;
}

// Initial Simulated MySQL Database state (matches schema.sql seed data)
interface MockIssue {
  id: number;
  code: string;
  title: string;
  categoryId: number;
  categoryName: string;
  locationId: number;
  locationName: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH';
  status: 'OPEN' | 'UNDER_REVIEW' | 'IN_PROGRESS' | 'RESOLVED';
  reportCount: number;
  assignedTo: string;
  createdAt: string;
}

interface MockComplaint {
  id: number;
  code: string;
  studentName: string;
  studentEmail: string;
  categoryId: number;
  categoryName: string;
  locationId: number;
  locationName: string;
  issueId: number | null;
  description: string;
  isAnonymous: boolean;
  priority: 'LOW' | 'MEDIUM' | 'HIGH';
  status: 'SUBMITTED' | 'UNDER_REVIEW' | 'IN_PROGRESS' | 'RESOLVED' | 'OPEN';
  createdAt: string;
}

interface MatchResult {
  issue: MockIssue;
  score: number;
  breakdown: { category: number; location: number; keyword: number; text: number };
}

const INITIAL_ISSUES: MockIssue[] = [
  {
    id: 1,
    code: 'CP-001',
    title: 'Block A WiFi Network Connectivity Problem',
    categoryId: 1,
    categoryName: 'Internet / WiFi',
    locationId: 1,
    locationName: 'Block A - 2nd Floor',
    priority: 'HIGH',
    status: 'IN_PROGRESS',
    reportCount: 5,
    assignedTo: 'Campus IT Infrastructure Team',
    createdAt: '2026-09-20 09:15:00'
  },
  {
    id: 2,
    code: 'CP-002',
    title: 'Drinking Water Cooler Filter Failure',
    categoryId: 3,
    categoryName: 'Water Supply',
    locationId: 2,
    locationName: 'Library Ground Floor',
    priority: 'MEDIUM',
    status: 'OPEN',
    reportCount: 2,
    assignedTo: 'Campus Facilities / Maintenance',
    createdAt: '2026-09-21 11:30:00'
  }
];

const INITIAL_COMPLAINTS: MockComplaint[] = [
  {
    id: 1,
    code: 'CMP-101',
    studentName: 'Rahul Verma',
    studentEmail: 'rahul@college.edu',
    categoryId: 1,
    categoryName: 'Internet / WiFi',
    locationId: 1,
    locationName: 'Block A - 2nd Floor',
    issueId: 1,
    description: 'WiFi is not working in Block A 2nd floor classrooms since morning.',
    isAnonymous: false,
    priority: 'HIGH',
    status: 'IN_PROGRESS',
    createdAt: '2026-09-20 09:15:00'
  },
  {
    id: 2,
    code: 'CMP-102',
    studentName: 'Ananya Sharma',
    studentEmail: 'ananya@college.edu',
    categoryId: 1,
    categoryName: 'Internet / WiFi',
    locationId: 1,
    locationName: 'Block A - 2nd Floor',
    issueId: 1,
    description: 'Block A second floor WiFi keeps disconnecting repeatedly during online lab test.',
    isAnonymous: false,
    priority: 'HIGH',
    status: 'IN_PROGRESS',
    createdAt: '2026-09-20 10:20:00'
  },
  {
    id: 3,
    code: 'CMP-103',
    studentName: 'Priya Nair',
    studentEmail: 'priya@college.edu',
    categoryId: 1,
    categoryName: 'Internet / WiFi',
    locationId: 1,
    locationName: 'Block A - 2nd Floor',
    issueId: 1,
    description: 'Internet router signal in Block A floor 2 is unavailable.',
    isAnonymous: true,
    priority: 'HIGH',
    status: 'IN_PROGRESS',
    createdAt: '2026-09-20 11:05:00'
  },
  {
    id: 4,
    code: 'CMP-104',
    studentName: 'Amit Patel',
    studentEmail: 'amit@college.edu',
    categoryId: 1,
    categoryName: 'Internet / WiFi',
    locationId: 1,
    locationName: 'Block A - 2nd Floor',
    issueId: 1,
    description: 'Cannot connect to college campus WiFi in Block A second floor.',
    isAnonymous: false,
    priority: 'HIGH',
    status: 'IN_PROGRESS',
    createdAt: '2026-09-20 13:45:00'
  },
  {
    id: 5,
    code: 'CMP-105',
    studentName: 'Kavita Sundaram',
    studentEmail: 'kavita@college.edu',
    categoryId: 1,
    categoryName: 'Internet / WiFi',
    locationId: 1,
    locationName: 'Block A - 2nd Floor',
    issueId: 1,
    description: 'WiFi network dropping packets continuously in Block A 2nd floor.',
    isAnonymous: false,
    priority: 'HIGH',
    status: 'IN_PROGRESS',
    createdAt: '2026-09-20 14:10:00'
  }
];

export default function App() {
  const [activeTab, setActiveTab] = useState<string>('app_demo');

  // Simulated Database State
  const [issues, setIssues] = useState<MockIssue[]>(INITIAL_ISSUES);
  const [complaints, setComplaints] = useState<MockComplaint[]>(INITIAL_COMPLAINTS);
  const [selectedIssueId, setSelectedIssueId] = useState<number>(1);

  // App Simulator State (Student vs Admin)
  const [currentRole, setCurrentRole] = useState<'STUDENT' | 'ADMIN'>('STUDENT');
  const [currentStudentName, setCurrentStudentName] = useState('Vikram Malhotra');
  const [currentStudentEmail, setCurrentStudentEmail] = useState('vikram@college.edu');

  // Student Complaint Form inputs
  const [newCatId, setNewCatId] = useState(1);
  const [newLocId, setNewLocId] = useState(1);
  const [newDesc, setNewDesc] = useState('');
  const [isAnon, setIsAnon] = useState(false);
  const [submitSuccessMsg, setSubmitSuccessMsg] = useState('');

  // Copy helper
  const [copiedCode, setCopiedCode] = useState<string | null>(null);
  const copyToClipboard = (text: string, id: string) => {
    navigator.clipboard.writeText(text);
    setCopiedCode(id);
    setTimeout(() => setCopiedCode(null), 2000);
  };

  // Calculate similarity algorithm live
  const checkSimilarityAgainstIssues = (catId: number, locId: number, text: string): MatchResult | null => {
    if (text.trim().length < 3) return null;
    const stopWords = new Set(['is', 'not', 'in', 'the', 'a', 'an', 'to', 'and', 'for', 'of', 'on', 'at', 'keeps', 'very', 'again', 'floor']);
    const getWords = (str: string) =>
      str.toLowerCase().replace(/[^a-z0-9\s]/g, '').split(/\s+/).filter(w => w.length > 2 && !stopWords.has(w));

    let bestMatch: MatchResult | null = null;

    issues.forEach(issue => {
      let score = 0;
      let catScore = 0;
      let locScore = 0;
      let keywordScore = 0;
      let textScore = 0;

      // 1. Category match (40 pts)
      if (catId === issue.categoryId) {
        score += 40;
        catScore = 40;
      }

      // 2. Location match (30 pts)
      if (locId === issue.locationId) {
        score += 30;
        locScore = 30;
      }

      // 3. Keyword overlap (20 pts)
      const words1 = getWords(text);
      const words2 = getWords(issue.title);
      const set1 = new Set(words1);
      const intersection = words2.filter(w => set1.has(w));
      const union = new Set([...words1, ...words2]);
      const jaccard = union.size > 0 ? intersection.length / union.size : 0;
      keywordScore = Math.round(jaccard * 20);
      score += keywordScore;

      // 4. Basic text overlap (10 pts)
      if (text.toLowerCase().includes('wifi') && issue.title.toLowerCase().includes('wifi')) {
        textScore = 10;
        score += 10;
      }

      if (score >= 60 && (!bestMatch || score > bestMatch.score)) {
        bestMatch = {
          issue,
          score,
          breakdown: { category: catScore, location: locScore, keyword: keywordScore, text: textScore }
        };
      }
    });

    return bestMatch;
  };

  const detectedMatch = checkSimilarityAgainstIssues(newCatId, newLocId, newDesc);

  // Submit Complaint in Simulator
  const handleSimulateSubmit = (joinIssueId?: number) => {
    if (newDesc.trim().length < 5) {
      setSubmitSuccessMsg('Please describe the campus problem in at least 5 characters.');
      return;
    }
    const nextComplaintId = complaints.length + 1;
    const complaintCode = `CMP-${100 + nextComplaintId}`;
    const categoryName = newCatId === 1 ? 'Internet / WiFi' : newCatId === 2 ? 'Classroom Equipment' : 'Water Supply';
    const locationName = newLocId === 1 ? 'Block A - 2nd Floor' : newLocId === 2 ? 'Library Ground Floor' : 'Computer Lab 3';

    const targetIssueId = joinIssueId !== undefined ? joinIssueId : (detectedMatch ? detectedMatch.issue.id : null);

    const newComplaintRecord: MockComplaint = {
      id: nextComplaintId,
      code: complaintCode,
      studentName: currentStudentName,
      studentEmail: currentStudentEmail,
      categoryId: newCatId,
      categoryName,
      locationId: newLocId,
      locationName,
      issueId: targetIssueId,
      description: newDesc,
      isAnonymous: isAnon,
      priority: 'HIGH',
      status: 'SUBMITTED',
      createdAt: 'Just now'
    };

    setComplaints([newComplaintRecord, ...complaints]);

    // Update issue count if joined
    if (targetIssueId) {
      setIssues(issues.map(iss => {
        if (iss.id === targetIssueId) {
          const updatedCount = iss.reportCount + 1;
          const updatedPriority: 'LOW' | 'MEDIUM' | 'HIGH' = updatedCount >= 5 ? 'HIGH' : updatedCount >= 3 ? 'MEDIUM' : 'LOW';
          return { ...iss, reportCount: updatedCount, priority: updatedPriority };
        }
        return iss;
      }));
    }

    setSubmitSuccessMsg(`Complaint ${complaintCode} logged successfully! ${targetIssueId ? `(Joined Issue CP-00${targetIssueId})` : ''}`);
    setTimeout(() => setSubmitSuccessMsg(''), 5000);
  };

  // Admin status update in Simulator
  const handleUpdateIssueStatus = (issueId: number, newStatus: MockIssue['status'], newPriority: MockIssue['priority']) => {
    setIssues(issues.map(iss => iss.id === issueId ? { ...iss, status: newStatus, priority: newPriority } : iss));
    // Cascade to linked complaints
    setComplaints(complaints.map(c => c.issueId === issueId ? { ...c, status: newStatus, priority: newPriority } : c));
  };

  const tabs: TabItem[] = [
    { id: 'app_demo', label: 'Campus Portal', icon: Play, badge: 'Student & Admin' },
    { id: 'workbench', label: 'Database Setup', icon: Database, badge: 'For setup only' }
  ];

  const currentIssue = issues.find(i => i.id === selectedIssueId) || issues[0];
  const complaintsForCurrentIssue = complaints.filter(c => c.issueId === selectedIssueId);

  return (
    <div className="min-h-screen bg-slate-50 text-slate-800 font-sans">
      {/* Top Header */}
      <header className="bg-white border-b border-slate-200 sticky top-0 z-30 shadow-xs">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 py-3 flex flex-col md:flex-row md:items-center md:justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-lg bg-indigo-600 flex items-center justify-center text-white font-bold text-xl shadow-sm">
              CP
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h1 className="text-xl font-bold text-slate-900 tracking-tight">CampusPulse</h1>
                <span className="px-2 py-0.5 text-xs font-semibold bg-emerald-100 text-emerald-800 rounded-md">
                  Java + MySQL Project
                </span>
                <span className="px-2 py-0.5 text-xs font-medium bg-indigo-100 text-indigo-700 rounded-md">
                  Advanced Java Lab
                </span>
              </div>
              <p className="text-xs text-slate-500">
                A simple way for students to report campus problems and track resolutions
              </p>
            </div>
          </div>

          <div className="flex items-center gap-3">
            <div className="flex items-center gap-2 bg-slate-100 px-3 py-1.5 rounded-lg border border-slate-200 text-xs">
              <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
              <span className="font-mono text-slate-700 font-medium">Student-first issue reporting</span>
            </div>
          </div>
        </div>

        {/* Navigation Tabs */}
        <div className="max-w-7xl mx-auto px-4 sm:px-6 flex overflow-x-auto gap-1 border-t border-slate-100 scrollbar-none">
          {tabs.map(tab => {
            const Icon = tab.icon;
            const isActive = activeTab === tab.id;
            return (
              <button
                key={tab.id}
                id={`tab-${tab.id}`}
                onClick={() => setActiveTab(tab.id)}
                className={`flex items-center gap-2 px-4 py-3 text-sm font-medium border-b-2 whitespace-nowrap transition-colors ${
                  isActive
                    ? 'border-indigo-600 text-indigo-600 bg-indigo-50/50'
                    : 'border-transparent text-slate-600 hover:text-slate-900 hover:bg-slate-100/60'
                }`}
              >
                <Icon className={`w-4 h-4 ${isActive ? 'text-indigo-600' : 'text-slate-400'}`} />
                {tab.label}
                {tab.badge && (
                  <span className={`text-[10px] px-1.5 py-0.2 rounded-full font-bold ${
                    isActive ? 'bg-indigo-200 text-indigo-800' : 'bg-slate-200 text-slate-700'
                  }`}>
                    {tab.badge}
                  </span>
                )}
              </button>
            );
          })}
        </div>
      </header>

      {/* Main Content Body */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 py-6">

        {/* TAB 1: MySQL Workbench & Setup */}
        {activeTab === 'workbench' && (
          <div className="space-y-6">
            {/* Quick Workbench Banner */}
            <div className="p-5 bg-gradient-to-r from-slate-900 to-indigo-950 text-white rounded-xl shadow-sm border border-slate-800">
              <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div>
                  <div className="flex items-center gap-2 mb-1">
                    <Database className="w-5 h-5 text-indigo-400" />
                    <h2 className="text-lg font-bold">MySQL Workbench Database Connection Guide</h2>
                  </div>
                  <p className="text-slate-300 text-sm max-w-2xl">
                    Follow these 4 simple steps in your local MySQL Workbench to initialize the database and run the CampusPulse Java backend.
                  </p>
                </div>
                <div className="flex items-center gap-2">
                  <button
                    onClick={() => copyToClipboard('mysql -u root -p campuspulse_db < schema.sql', 'cli-cmd')}
                    className="flex items-center gap-1.5 px-3 py-2 bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-semibold rounded-lg transition-colors"
                  >
                    {copiedCode === 'cli-cmd' ? <Check className="w-4 h-4" /> : <Copy className="w-4 h-4" />}
                    Copy Terminal Command
                  </button>
                </div>
              </div>
            </div>

            {/* Step-by-Step Instructions */}
            <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
              <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-xs">
                <div className="w-7 h-7 rounded-full bg-indigo-100 text-indigo-700 font-bold flex items-center justify-center text-xs mb-2">1</div>
                <h3 className="font-bold text-slate-900 text-sm mb-1">Open MySQL Workbench</h3>
                <p className="text-xs text-slate-500 mb-2">Connect to your local MySQL instance (usually on port 3306 with user <code>root</code>).</p>
                <div className="bg-slate-50 p-2 rounded text-[11px] font-mono text-slate-700 border border-slate-200">
                  Host: localhost:3306<br/>
                  User: root
                </div>
              </div>

              <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-xs">
                <div className="w-7 h-7 rounded-full bg-indigo-100 text-indigo-700 font-bold flex items-center justify-center text-xs mb-2">2</div>
                <h3 className="font-bold text-slate-900 text-sm mb-1">Execute schema.sql</h3>
                <p className="text-xs text-slate-500 mb-2">Open the <code>schema.sql</code> file located in project root and click the <strong>Execute (Lightning)</strong> icon.</p>
                <div className="bg-slate-50 p-2 rounded text-[11px] font-mono text-slate-700 border border-slate-200">
                  Creates campuspulse_db &amp; 7 tables
                </div>
              </div>

              <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-xs">
                <div className="w-7 h-7 rounded-full bg-indigo-100 text-indigo-700 font-bold flex items-center justify-center text-xs mb-2">3</div>
                <h3 className="font-bold text-slate-900 text-sm mb-1">Configure db.properties</h3>
                <p className="text-xs text-slate-500 mb-2">Set your local MySQL root password in <code>campuspulse-java/src/main/resources/db.properties</code>.</p>
                <div className="bg-slate-50 p-2 rounded text-[11px] font-mono text-slate-700 border border-slate-200">
                  db.password=YOUR_PASSWORD
                </div>
              </div>

              <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-xs">
                <div className="w-7 h-7 rounded-full bg-indigo-100 text-indigo-700 font-bold flex items-center justify-center text-xs mb-2">4</div>
                <h3 className="font-bold text-slate-900 text-sm mb-1">Deploy on Tomcat 10.1+</h3>
                <p className="text-xs text-slate-500 mb-2">Run <code>mvn clean package</code> or Run on Server in Eclipse/IntelliJ with Apache Tomcat 10.1+.</p>
                <div className="bg-slate-50 p-2 rounded text-[11px] font-mono text-slate-700 border border-slate-200">
                  http://localhost:8080/campuspulse
                </div>
              </div>
            </div>

            {/* Live Interactive Table Browser */}
            <div className="bg-white rounded-xl border border-slate-200 shadow-xs overflow-hidden">
              <div className="p-4 border-b border-slate-200 bg-slate-50 flex items-center justify-between flex-wrap gap-2">
                <div className="flex items-center gap-2">
                  <Terminal className="w-4 h-4 text-indigo-600" />
                  <span className="font-bold text-sm text-slate-800">MySQL Table Inspector: `issues` &amp; `complaints`</span>
                  <span className="text-xs text-slate-500 font-mono">(Simulated Workbench Result Grid)</span>
                </div>
                <div className="flex items-center gap-2 text-xs">
                  <span className="text-slate-500">Query:</span>
                  <code className="bg-white px-2 py-0.5 rounded border border-slate-200 text-indigo-700 font-mono">
                    SELECT * FROM issues ORDER BY report_count DESC;
                  </code>
                </div>
              </div>

              <div className="overflow-x-auto">
                <table className="w-full text-left text-xs border-collapse">
                  <thead>
                    <tr className="bg-slate-100 text-slate-700 border-b border-slate-200 font-semibold">
                      <th className="p-2.5">id</th>
                      <th className="p-2.5">issue_code</th>
                      <th className="p-2.5">title</th>
                      <th className="p-2.5">category</th>
                      <th className="p-2.5">location</th>
                      <th className="p-2.5 text-center">report_count</th>
                      <th className="p-2.5">priority</th>
                      <th className="p-2.5">status</th>
                      <th className="p-2.5">assigned_to</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-200">
                    {issues.map(iss => (
                      <tr key={iss.id} className="hover:bg-slate-50 font-mono text-slate-800">
                        <td className="p-2.5 font-bold text-slate-500">{iss.id}</td>
                        <td className="p-2.5 text-indigo-600 font-semibold">{iss.code}</td>
                        <td className="p-2.5 font-sans font-medium text-slate-900">{iss.title}</td>
                        <td className="p-2.5 font-sans">{iss.categoryName}</td>
                        <td className="p-2.5 font-sans">{iss.locationName}</td>
                        <td className="p-2.5 text-center">
                          <span className="px-2 py-0.5 bg-indigo-100 text-indigo-800 rounded-full font-bold">
                            {iss.reportCount}
                          </span>
                        </td>
                        <td className="p-2.5">
                          <span className={`px-2 py-0.5 rounded text-[11px] font-bold ${
                            iss.priority === 'HIGH' ? 'bg-red-100 text-red-700' : 'bg-amber-100 text-amber-700'
                          }`}>
                            {iss.priority}
                          </span>
                        </td>
                        <td className="p-2.5">
                          <span className={`px-2 py-0.5 rounded text-[11px] font-bold ${
                            iss.status === 'RESOLVED' ? 'bg-emerald-100 text-emerald-700' : 'bg-blue-100 text-blue-700'
                          }`}>
                            {iss.status}
                          </span>
                        </td>
                        <td className="p-2.5 font-sans text-slate-600">{iss.assignedTo}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              {/* Seed Data explanation */}
              <div className="p-4 bg-slate-50 border-t border-slate-200 text-xs text-slate-600 flex items-start gap-2">
                <CheckCircle2 className="w-4 h-4 text-emerald-600 flex-shrink-0 mt-0.5" />
                <div>
                  <strong>5-Student WiFi in Block A Scenario Pre-Loaded:</strong> The schema includes Rahul, Ananya, Priya, Amit, and Kavita who each submitted separate complaints about the WiFi in Block A 2nd Floor. CampusPulse automatically grouped them into Issue <code>CP-001</code> and escalated priority to <strong>HIGH</strong>.
                </div>
              </div>
            </div>
          </div>
        )}

        {/* TAB 2: Live App Simulator (MVC) */}
        {activeTab === 'app_demo' && (
          <div className="space-y-6">
            {/* Role Switcher Toolbar */}
            <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-xs flex flex-wrap items-center justify-between gap-4">
              <div className="flex items-center gap-3">
                <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Choose a view:</span>
                <div className="inline-flex rounded-lg border border-slate-200 p-1 bg-slate-50">
                  <button
                    onClick={() => setCurrentRole('STUDENT')}
                    className={`px-3 py-1.5 rounded-md text-xs font-semibold transition-colors flex items-center gap-1.5 ${
                      currentRole === 'STUDENT' ? 'bg-indigo-600 text-white shadow-xs' : 'text-slate-600 hover:text-slate-900'
                    }`}
                  >
                    <UserCheck className="w-3.5 h-3.5" /> Student
                  </button>
                  <button
                    onClick={() => setCurrentRole('ADMIN')}
                    className={`px-3 py-1.5 rounded-md text-xs font-semibold transition-colors flex items-center gap-1.5 ${
                      currentRole === 'ADMIN' ? 'bg-red-600 text-white shadow-xs' : 'text-slate-600 hover:text-slate-900'
                    }`}
                  >
                    <ShieldCheck className="w-3.5 h-3.5" /> Campus Admin
                  </button>
                </div>
              </div>

              <div className="text-xs text-slate-500">
                Viewing as: <strong className="text-slate-900">{currentRole === 'STUDENT' ? currentStudentName : 'Prof. Sharma (Campus Admin)'}</strong>
              </div>
            </div>

            <div className="rounded-lg border border-blue-200 bg-blue-50 px-4 py-3 text-sm text-blue-900 flex items-start gap-2">
              <AlertCircle className="w-4 h-4 mt-0.5 flex-shrink-0" />
              <p><strong>Interactive preview:</strong> this screen uses sample data in your browser. The Java/JSP application and MySQL database are included separately in the project.</p>
            </div>

            {submitSuccessMsg && (
              <div className={`p-3 border rounded-lg text-sm flex items-center gap-2 ${submitSuccessMsg.startsWith('Please') ? 'bg-amber-50 text-amber-800 border-amber-200' : 'bg-emerald-50 text-emerald-800 border-emerald-200'}`}>
                <CheckCircle2 className="w-4 h-4 text-emerald-600" />
                {submitSuccessMsg}
              </div>
            )}

            {/* STUDENT PORTAL VIEW */}
            {currentRole === 'STUDENT' && (
              <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
                {/* Submit Complaint Form */}
                <div className="lg:col-span-6 bg-white p-5 rounded-xl border border-slate-200 shadow-xs">
                  <div className="flex items-center justify-between pb-3 mb-4 border-b border-slate-100">
                    <div>
                      <h3 className="font-bold text-slate-900 text-base">Report a Campus Issue</h3>
                      <p className="text-xs text-slate-500">Tell us what is wrong on campus. We will route it to the responsible team.</p>
                    </div>
                    <span className="text-[11px] font-bold bg-indigo-50 text-indigo-700 px-2 py-0.5 rounded border border-indigo-200">
                      Report an issue
                    </span>
                  </div>

                  <div className="space-y-4">
                    <div>
                      <label className="block text-xs font-semibold text-slate-700 mb-1">What kind of problem is it?</label>
                      <select
                        value={newCatId}
                        onChange={e => setNewCatId(Number(e.target.value))}
                        className="w-full text-xs p-2.5 rounded-lg border border-slate-200 bg-white focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                      >
                        <option value={1}>Internet or Wi-Fi</option>
                        <option value={2}>Classroom equipment (fan, projector, etc.)</option>
                        <option value={3}>Water supply or cooler</option>
                      </select>
                    </div>

                    <div>
                      <label className="block text-xs font-semibold text-slate-700 mb-1">Where is the problem?</label>
                      <select
                        value={newLocId}
                        onChange={e => setNewLocId(Number(e.target.value))}
                        className="w-full text-xs p-2.5 rounded-lg border border-slate-200 bg-white focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                      >
                        <option value={1}>Block A, 2nd floor</option>
                        <option value={2}>Library, ground floor</option>
                        <option value={3}>Computer Lab 3</option>
                      </select>
                    </div>

                    <div>
                      <label className="block text-xs font-semibold text-slate-700 mb-1">Describe what happened</label>
                      <textarea
                        rows={3}
                        value={newDesc}
                        onChange={e => setNewDesc(e.target.value)}
                        className="w-full text-xs p-2.5 rounded-lg border border-slate-200 bg-white focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                        placeholder="For example: Wi-Fi is not working in Block A since this morning."
                      />
                    </div>

                    <div className="flex items-center gap-2 p-2.5 bg-slate-50 rounded-lg border border-slate-200">
                      <input
                        type="checkbox"
                        id="anonCheck"
                        checked={isAnon}
                        onChange={e => setIsAnon(e.target.checked)}
                        className="rounded text-indigo-600"
                      />
                      <label htmlFor="anonCheck" className="text-xs text-slate-700 cursor-pointer">
                        <strong>Report Anonymously</strong> (hide student name from administrator UI)
                      </label>
                    </div>

                    {/* Dynamic Similarity Detection Box */}
                    {detectedMatch && (
                      <div className="p-3 bg-amber-50 border border-amber-200 rounded-lg space-y-2">
                        <div className="flex items-start gap-2">
                          <AlertCircle className="w-4 h-4 text-amber-600 flex-shrink-0 mt-0.5" />
                          <div>
                            <div className="text-xs font-bold text-amber-900">
                              Duplicate / Similar Issue Detected ({detectedMatch.score}/100 pts)
                            </div>
                            <div className="text-xs text-amber-800 mt-0.5">
                              Issue <strong>{detectedMatch.issue.code}</strong>: {detectedMatch.issue.title} already has <strong>{detectedMatch.issue.reportCount} student reports</strong>.
                            </div>
                          </div>
                        </div>

                        <div className="grid grid-cols-4 gap-1 text-[10px] text-amber-800 font-mono bg-white/70 p-1.5 rounded border border-amber-200">
                          <div>Cat: {detectedMatch.breakdown.category}/40</div>
                          <div>Loc: {detectedMatch.breakdown.location}/30</div>
                          <div>Keywords: {detectedMatch.breakdown.keyword}/20</div>
                          <div>Text: {detectedMatch.breakdown.text}/10</div>
                        </div>

                        <div className="flex items-center gap-2 pt-1">
                          <button
                            type="button"
                            onClick={() => handleSimulateSubmit(detectedMatch.issue.id)}
                            className="px-3 py-1.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded text-xs font-semibold flex items-center gap-1 transition-colors"
                          >
                            <CheckCircle2 className="w-3.5 h-3.5" /> Join Existing Issue (Recommended)
                          </button>
                          <button
                            type="button"
                            onClick={() => handleSimulateSubmit()}
                            className="px-2.5 py-1.5 bg-white hover:bg-slate-100 text-slate-700 border border-slate-300 rounded text-xs"
                          >
                            Submit Separately
                          </button>
                        </div>
                      </div>
                    )}

                    {!detectedMatch && (
                      <button
                        type="button"
                        onClick={() => handleSimulateSubmit()}
                        className="w-full py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold text-xs rounded-lg shadow-sm transition-colors flex items-center justify-center gap-2"
                      >
                        <PlusCircle className="w-4 h-4" /> Send Report
                      </button>
                    )}
                  </div>
                </div>

                {/* Student's Submitted Complaints */}
                <div className="lg:col-span-6 bg-white p-5 rounded-xl border border-slate-200 shadow-xs">
                  <h3 className="font-bold text-slate-900 text-base mb-1">My Submitted Complaints</h3>
                  <p className="text-xs text-slate-500 mb-3">Check the reports and updates shown in this preview.</p>

                  <div className="space-y-3 max-h-[460px] overflow-y-auto pr-1">
                    {complaints.map(c => (
                      <div key={c.id} className="p-3 bg-slate-50 border border-slate-200 rounded-lg text-xs space-y-1">
                        <div className="flex items-center justify-between">
                          <span className="font-mono font-bold text-indigo-700">{c.code}</span>
                          <span className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${
                            c.status === 'RESOLVED' ? 'bg-emerald-100 text-emerald-800' :
                            c.status === 'IN_PROGRESS' ? 'bg-amber-100 text-amber-800' : 'bg-blue-100 text-blue-800'
                          }`}>
                            {c.status}
                          </span>
                        </div>
                        <p className="text-slate-800 font-medium">{c.description}</p>
                        <div className="flex items-center justify-between text-[11px] text-slate-500 pt-1 border-t border-slate-200/60">
                          <span>{c.locationName} • {c.categoryName}</span>
                          {c.issueId ? (
                            <span className="text-indigo-600 font-semibold font-mono">Linked: CP-00{c.issueId}</span>
                          ) : (
                            <span className="text-slate-400">Independent</span>
                          )}
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            )}

            {/* ADMIN PORTAL VIEW */}
            {currentRole === 'ADMIN' && (
              <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
                {/* Left: Issue Cluster Details */}
                <div className="lg:col-span-8 bg-white p-5 rounded-xl border border-slate-200 shadow-xs">
                  <div className="flex items-center justify-between pb-3 mb-4 border-b border-slate-100">
                    <div>
                      <div className="flex items-center gap-2">
                        <span className="font-mono font-bold text-indigo-700 bg-indigo-50 px-2 py-0.5 rounded border border-indigo-200">
                          {currentIssue.code}
                        </span>
                        <h3 className="font-bold text-slate-900 text-base">{currentIssue.title}</h3>
                      </div>
                      <p className="text-xs text-slate-500 mt-0.5">{currentIssue.locationName} • {currentIssue.categoryName}</p>
                    </div>
                    <span className="px-3 py-1 rounded-full text-xs font-bold bg-indigo-600 text-white">
                      {currentIssue.reportCount} Student Complaints Clustered
                    </span>
                  </div>

                  {/* Student complaints clustered under this issue */}
                  <div className="space-y-3">
                    <h4 className="font-semibold text-xs text-slate-700 uppercase tracking-wider">
                      Clustered Student Reports ({complaintsForCurrentIssue.length})
                    </h4>

                    <div className="space-y-2 max-h-[360px] overflow-y-auto pr-1">
                      {complaintsForCurrentIssue.map(c => (
                        <div key={c.id} className="p-3 bg-slate-50 rounded-lg border border-slate-200 text-xs">
                          <div className="flex items-center justify-between mb-1">
                            <span className="font-mono text-indigo-600 font-bold">{c.code}</span>
                            <span className="text-slate-400 text-[11px]">{c.createdAt}</span>
                          </div>
                          <p className="text-slate-900 font-medium mb-1.5">{c.description}</p>
                          <div className="flex items-center justify-between text-slate-500 text-[11px]">
                            <span>
                              {c.isAnonymous ? (
                                <span className="text-slate-400 italic">Anonymous Student</span>
                              ) : (
                                <strong>{c.studentName} ({c.studentEmail})</strong>
                              )}
                            </span>
                            <span className="font-mono">{c.status}</span>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                </div>

                {/* Right: Administrative Action Controls */}
                <div className="lg:col-span-4 bg-white p-5 rounded-xl border border-slate-200 shadow-xs space-y-4">
                  <h3 className="font-bold text-slate-900 text-sm pb-2 border-b border-slate-100">
                    Resolution &amp; Cascading Status
                  </h3>

                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">Priority Override</label>
                    <select
                      value={currentIssue.priority}
                      onChange={e => handleUpdateIssueStatus(currentIssue.id, currentIssue.status, e.target.value as any)}
                      className="w-full text-xs p-2 rounded-lg border border-slate-200 bg-white"
                    >
                      <option value="LOW">LOW</option>
                      <option value="MEDIUM">MEDIUM</option>
                      <option value="HIGH">HIGH (Urgent Attention)</option>
                    </select>
                  </div>

                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">Status Workflow</label>
                    <div className="space-y-2">
                      {(['OPEN', 'UNDER_REVIEW', 'IN_PROGRESS', 'RESOLVED'] as const).map(st => (
                        <button
                          key={st}
                          onClick={() => handleUpdateIssueStatus(currentIssue.id, st, currentIssue.priority)}
                          className={`w-full text-left px-3 py-2 rounded-lg text-xs font-semibold border transition-colors flex items-center justify-between ${
                            currentIssue.status === st
                              ? 'bg-indigo-50 border-indigo-600 text-indigo-700 font-bold'
                              : 'bg-white border-slate-200 text-slate-600 hover:bg-slate-50'
                          }`}
                        >
                          <span>{st}</span>
                          {currentIssue.status === st && <Check className="w-3.5 h-3.5 text-indigo-600" />}
                        </button>
                      ))}
                    </div>
                    <p className="text-[11px] text-slate-500 mt-2">
                      Updating this cascades to all <strong>{currentIssue.reportCount}</strong> student complaints via <code>IssueDAO.updateStatusAndPriority()</code> in an ACID transaction.
                    </p>
                  </div>
                </div>
              </div>
            )}
          </div>
        )}

      </main>

      {/* Footer */}
      <footer className="max-w-7xl mx-auto px-4 sm:px-6 py-6 border-t border-slate-200 text-xs text-slate-500 flex flex-col sm:flex-row items-center justify-between gap-2">
        <div>CampusPulse • Advanced Java Project for College Lab Evaluation</div>
        <div className="font-mono text-slate-400">Student portal preview • Java/JSP backend included</div>
      </footer>
    </div>
  );
}
